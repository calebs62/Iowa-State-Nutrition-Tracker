package com.example.a1_jubair_6_frontend;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.not;

import android.content.Context;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;

import com.example.a1_jubair_6_frontend.activities.LoginSignupActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AlexanderSvobodnySystemTest {

    private UiDevice device;

    @Rule
    public ActivityScenarioRule<LoginSignupActivity> activityRule =
            new ActivityScenarioRule<>(LoginSignupActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.POST_NOTIFICATIONS,
            android.Manifest.permission.SEND_SMS
    );

    @Before
    public void setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Disable animations
        try {
            device.executeShellCommand("settings put global window_animation_scale 0");
            device.executeShellCommand("settings put global transition_animation_scale 0");
            device.executeShellCommand("settings put global animator_duration_scale 0");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void allowPermissionsIfNeeded() throws Exception {
        UiObject allowButton = device.findObject(new UiSelector()
                .clickable(true)
                .checkable(false)
                .textMatches("(?i)allow|permit|ok"));
        if (allowButton.exists()) {
            allowButton.click();
        }
    }

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

                    for (String prefsFile : prefsFiles) {
                        context.getSharedPreferences(prefsFile, Context.MODE_PRIVATE)
                                .edit()
                                .clear()
                                .commit();
                    }

                    // Clear app data
                    context.getCacheDir().delete();

                    try {
                        base.evaluate();
                    } finally {
                        // Clear again after test
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
    public void testCompleteNotificationFlow() throws Exception {
        // Login
        onView(withId(R.id.emailText))
                .perform(typeText("svobodny@iastate.edu"), closeSoftKeyboard());
        Thread.sleep(300);

        onView(withId(R.id.passwordText))
                .perform(typeText("ilove309"), closeSoftKeyboard());
        Thread.sleep(300);

        onView(withId(R.id.btnExplore))
                .perform(click());
        Thread.sleep(3000);

        // Navigate to Profile
        onView(withId(R.id.nav_profile))
                .perform(click());
        Thread.sleep(1000);

        // Click on notifications
        onView(withId(R.id.notifications))
                .perform(click());
        Thread.sleep(1000);

        // Handle any permission dialogs
        allowPermissionsIfNeeded();

        // Test system notification toggle
        onView(withId(R.id.switchSystemNotifications))
                .perform(click());
        Thread.sleep(500);
        allowPermissionsIfNeeded();

        // Verify other switches are disabled when system notifications are off
        onView(withId(R.id.switchPushNotifications))
                .check(matches(not(isEnabled())));
        onView(withId(R.id.switchReminders))
                .check(matches(not(isEnabled())));

        // Turn system notifications back on
        onView(withId(R.id.switchSystemNotifications))
                .perform(click());
        Thread.sleep(500);
        allowPermissionsIfNeeded();

        // Test push notifications
        onView(withId(R.id.switchPushNotifications))
                .check(matches(isEnabled()))
                .perform(click());
        Thread.sleep(500);
        allowPermissionsIfNeeded();

        // Test reminders
        onView(withId(R.id.switchReminders))
                .check(matches(isEnabled()))
                .perform(click());
        Thread.sleep(500);

        // Fill in test notification
        onView(withId(R.id.notificationTitle))
                .perform(scrollTo(), typeText("System Test"), closeSoftKeyboard());
        Thread.sleep(300);

        onView(withId(R.id.notificationMessage))
                .perform(scrollTo(), typeText("Testing notification system"), closeSoftKeyboard());
        Thread.sleep(300);

        // Select email notification type
        onView(withId(R.id.radioEmail))
                .perform(scrollTo(), click());
        Thread.sleep(300);

        // Send test notification
        onView(withId(R.id.testNotificationButton))
                .perform(scrollTo(), click());
        Thread.sleep(1000);

        // Verify final state of switches
        onView(withId(R.id.switchSystemNotifications))
                .perform(scrollTo())
                .check(matches(isChecked()));
        onView(withId(R.id.switchPushNotifications))
                .perform(scrollTo())
                .check(matches(isChecked()));
        onView(withId(R.id.switchReminders))
                .perform(scrollTo())
                .check(matches(isChecked()));
    }

    @Test
    public void testMenuNavigationAndFiltering() throws InterruptedException {
        // Login
        onView(withId(R.id.emailText))
                .perform(typeText("svobodny@iastate.edu"), closeSoftKeyboard());
        Thread.sleep(300);

        onView(withId(R.id.passwordText))
                .perform(typeText("ilove309"), closeSoftKeyboard());
        Thread.sleep(300);

        onView(withId(R.id.btnExplore))
                .perform(click());
        Thread.sleep(3000);

        // Go to Menus
        onView(withId(R.id.nav_menus))
                .perform(click());
        Thread.sleep(1000);

        // Test meal type tabs navigation
        onView(withId(R.id.mealTypeTabs))
                .check(matches(isDisplayed()));
        Thread.sleep(500);

        // Test tab navigation
        onView(withText("Breakfast"))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(500);

        onView(withText("Lunch"))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(500);

        onView(withText("Dinner"))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(500);

        // Test search functionality
        onView(withId(R.id.searchBar))
                .check(matches(isDisplayed()))
                .perform(typeText("chicken"), closeSoftKeyboard());
        Thread.sleep(1000);

        // Show filters
        onView(withId(R.id.btnShowFilters))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(1000);

        // Test nutrition filter chips
        onView(withId(R.id.chipLowFat))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(500);

        onView(withId(R.id.chipLowCarb))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(500);

        onView(withId(R.id.chipLowSodium))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(500);

        // Test sorting
        onView(withId(R.id.btnSortName))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(500);

        onView(withId(R.id.btnSortCalories))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(500);

        onView(withId(R.id.btnSortProtein))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(500);

        // Test menu viewing
        onView(withId(R.id.btnViewMenus))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(1000);

        // Verify were in the menu view
        onView(withId(R.id.menuSpinner))
                .check(matches(isDisplayed()));
    }


    @Test
    public void testGroupSearchAndForm() throws InterruptedException {
        // Login
        onView(withId(R.id.emailText))
                .perform(typeText("svobodny@iastate.edu"), closeSoftKeyboard());
        Thread.sleep(300);

        onView(withId(R.id.passwordText))
                .perform(typeText("ilove309"), closeSoftKeyboard());
        Thread.sleep(300);

        onView(withId(R.id.btnExplore))
                .perform(click());
        Thread.sleep(3000);

        // Go to home and wait for content to load
        onView(withId(R.id.nav_home))
                .perform(click());
        Thread.sleep(3000);

        // Try to click view groups button with more robust approach
        onView(withId(R.id.btnViewGroups))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(2000);

        // Test group search
        onView(withId(R.id.searchGroupsInput))
                .check(matches(isDisplayed()))
                .perform(typeText("Weight"), closeSoftKeyboard());
        Thread.sleep(1000);

        // Verify groups list is there
        onView(withId(R.id.groupsList))
                .check(matches(isDisplayed()));
        Thread.sleep(1000);

        // Click create new group to check form
        onView(withId(R.id.createGroupButton))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(1000);

        // Verify form fields are displayed
        onView(withId(R.id.groupNameInput))
                .check(matches(isDisplayed()));

        onView(withId(R.id.planNameInput))
                .check(matches(isDisplayed()));

        onView(withId(R.id.caloriesInput))
                .check(matches(isDisplayed()));

        onView(withId(R.id.proteinInput))
                .check(matches(isDisplayed()));

        // Cancel out of the form
        onView(withText("Cancel"))
                .perform(click());
        Thread.sleep(1000);

        // Verify were back at the groups list
        onView(withId(R.id.groupsList))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testMenuFilteringAndSearch() throws InterruptedException {
        // Login
        onView(withId(R.id.emailText))
                .perform(typeText("svobodny@iastate.edu"), closeSoftKeyboard());
        Thread.sleep(300);

        onView(withId(R.id.passwordText))
                .perform(typeText("ilove309"), closeSoftKeyboard());
        Thread.sleep(300);

        onView(withId(R.id.btnExplore))
                .perform(click());
        Thread.sleep(3000);

        // Navigate to menus
        onView(withId(R.id.nav_menus))
                .perform(click());
        Thread.sleep(1000);

        // Check the title is visible
        onView(withId(R.id.titleMenus))
                .check(matches(isDisplayed()));

        // Test search functionality
        onView(withId(R.id.searchBar))
                .check(matches(isDisplayed()))
                .perform(typeText("chicken"), closeSoftKeyboard());
        Thread.sleep(1000);

        // Show advanced filters
        onView(withId(R.id.btnShowFilters))
                .perform(click());
        Thread.sleep(1000);

        // Test meal tabs
        onView(withText("Breakfast"))
                .perform(click());
        Thread.sleep(500);

        onView(withText("Lunch"))
                .perform(click());
        Thread.sleep(500);

        onView(withText("Dinner"))
                .perform(click());
        Thread.sleep(500);

        // Test nutrition filter chips
        onView(withId(R.id.chipLowFat))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(500);

        onView(withId(R.id.chipLowCarb))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(500);

        onView(withId(R.id.chipHighProtein))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(500);

        // Test sorting
        onView(withId(R.id.btnSortName))
                .perform(click());
        Thread.sleep(500);

        onView(withId(R.id.btnSortCalories))
                .perform(click());
        Thread.sleep(500);

        onView(withId(R.id.btnSortProtein))
                .perform(click());
        Thread.sleep(500);

        // Reset filters
        onView(withId(R.id.btnResetFilters))
                .perform(click());
        Thread.sleep(500);

        // Verify food list is displayed
        onView(withId(R.id.foodList))
                .check(matches(isDisplayed()));
    }
}
