package com.example.a1_jubair_6_frontend;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;

import android.content.Context;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.a1_jubair_6_frontend.activities.LoginSignupActivity;
import com.example.a1_jubair_6_frontend.models.FoodItem;
import com.example.a1_jubair_6_frontend.models.Menu;
import com.example.a1_jubair_6_frontend.models.NotificationSettings;
import com.example.a1_jubair_6_frontend.models.PrivacySettings;
import com.example.a1_jubair_6_frontend.models.User;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.HashSet;

public class BasicInstrumentedTests {
    @Rule
    public ActivityScenarioRule<LoginSignupActivity> activityRule =
            new ActivityScenarioRule<>(LoginSignupActivity.class);

    @Rule
    public TestRule clearPreferencesRule = new TestRule() {
        @Override
        public Statement apply(final Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    Context context = InstrumentationRegistry.getInstrumentation()
                            .getTargetContext().getApplicationContext();

                    String[] prefsFiles = {
                            "ProfilePreferences",
                            "PREFS",
                            "user_prefs",
                            "app_prefs",
                            "login_prefs"
                    };

                    // Clear preferences before test
                    for (String prefsFile : prefsFiles) {
                        context.getSharedPreferences(prefsFile, Context.MODE_PRIVATE)
                                .edit()
                                .clear()
                                .commit();
                    }

                    context.getCacheDir().delete();

                    try {
                        base.evaluate();
                    } finally {
                        // Clear preferences after test
                        for (String prefsFile : prefsFiles) {
                            context.getSharedPreferences(prefsFile, Context.MODE_PRIVATE)
                                    .edit()
                                    .clear()
                                    .commit();
                        }
                    }
                }
            };
        }
    };

    @Test
    public void testLoginFlow() throws InterruptedException {
        // Test login UI flow
        onView(withId(R.id.emailText))
                .perform(typeText("svobodny@iastate.edu"), closeSoftKeyboard());
        Thread.sleep(300);

        onView(withId(R.id.passwordText))
                .perform(typeText("ilove309"), closeSoftKeyboard());
        Thread.sleep(300);

        onView(withId(R.id.btnExplore))
                .perform(click());
        Thread.sleep(300);
    }

    @Test
    public void testDataModels() {
        // Test FoodItem model
        FoodItem foodItem = new FoodItem("Test Food", 100, 5, 200, 15, 10, "1 serving", "Test description");
        assert foodItem.getName().equals("Test Food");
        assert foodItem.getCalories() == 100;
        assert foodItem.getProtein() == 10;

        // Test Menu model
        Menu menu = new Menu("Test Menu", "Location", "Lunch", "2024-03-20");
        menu.setFoodItems(new HashSet<>());
        assert menu.getName().equals("Test Menu");
        assert menu.getMeal().equals("Lunch");

        // Test User model
        User user = new User(1, "testuser", "pass123", "John", "Doe", 72, 180, User.Account.USER);
        assert user.getId() == 1;
        assert user.getUsername().equals("testuser");
        assert user.getFname().equals("John");

        // Test PrivacySettings model
        PrivacySettings privacySettings = new PrivacySettings(true, true, true);
        assert privacySettings.getFood();
        assert privacySettings.getGoal();
        assert privacySettings.getAchievement();

        // Test NotificationSettings model
        NotificationSettings notifSettings = new NotificationSettings();
        notifSettings.setSystemNotification(true);
        notifSettings.setTimeNotification(true);
        assert notifSettings.isSystemNotification();
        assert notifSettings.isTimeNotification();
    }

    @Test
    public void testNavigationFlow() throws InterruptedException {
        // Login first
        testLoginFlow();
        Thread.sleep(1000);

        // Test navigation to different sections
        onView(withId(R.id.nav_menus))
                .perform(click());
        Thread.sleep(500);

        onView(withId(R.id.nav_tracker))
                .perform(click());
        Thread.sleep(500);

        onView(withId(R.id.nav_goals))
                .perform(click());
        Thread.sleep(500);

        onView(withId(R.id.nav_profile))
                .perform(click());
        Thread.sleep(500);
    }

    @Test
    public void testBasicProfileFunctionality() throws InterruptedException {
        // Login first
        testLoginFlow();
        Thread.sleep(1000);

        // Navigate to profile
        onView(withId(R.id.nav_profile))
                .perform(click());
        Thread.sleep(500);

        // Test profile navigation
        onView(withId(R.id.personalInfo))
                .perform(click());
        Thread.sleep(500);

        // Verify personal info screen elements
        onView(withId(R.id.btnEditWeight))
                .check(matches(isDisplayed()));
        onView(withId(R.id.btnEditHeight))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testActivityFeed() throws InterruptedException {
        // Login first
        onView(withId(R.id.emailText))
                .perform(typeText("svobodny@iastate.edu"), closeSoftKeyboard());
        Thread.sleep(300);

        onView(withId(R.id.passwordText))
                .perform(typeText("ilove309"), closeSoftKeyboard());
        Thread.sleep(300);

        onView(withId(R.id.btnExplore))
                .perform(click());
        Thread.sleep(3000);

        // Check if we need to join a group
        try {
            // We're not in a group, need to join one
            onView(withId(R.id.btnViewGroups))
                    .perform(click());
            Thread.sleep(1000);

            // Search for Lose_Weight group
            onView(withId(R.id.searchGroupsInput))
                    .perform(typeText("Lose_Weight"), closeSoftKeyboard());
            Thread.sleep(1000);

            // Create a matcher for the join button within the card containing Lose_Weight
            onView(allOf(
                    withId(R.id.joinButton),
                    isDescendantOfA(hasDescendant(withText("Lose_Weight")))
            )).perform(click());
            Thread.sleep(2000);

        }
        catch (Exception e) {

        }

        // Navigate to tracker (activity feed)
        onView(withId(R.id.nav_tracker))
                .perform(click());
        Thread.sleep(1000);

        // Verify activity feed elements are displayed
        onView(withId(R.id.activityFeedRecyclerView))
                .check(matches(isDisplayed()));
        Thread.sleep(500);

        // Test scroll loading by scrolling to the bottom
        onView(withId(R.id.activityFeedRecyclerView))
                .perform(ViewActions.swipeUp());
        Thread.sleep(1000);

        onView(withId(R.id.activityFeedRecyclerView))
                .perform(ViewActions.swipeUp());
        Thread.sleep(1000);

        // Leave group at end of test
        onView(withId(R.id.nav_home))
                .perform(click());
        Thread.sleep(1000);

        onView(withId(R.id.btnEnter))
                .perform(scrollTo(), click());
        Thread.sleep(1000);

        onView(withId(R.id.leaveGroupButton))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(1000);
    }
}
