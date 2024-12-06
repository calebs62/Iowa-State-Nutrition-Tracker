package com.example.a1_jubair_6_frontend;

import static android.app.PendingIntent.getActivity;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;

import androidx.test.espresso.action.ViewActions;
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
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CalebSanchezSystemTest {

    private UiDevice device;

    @Rule
    public ActivityScenarioRule<LoginSignupActivity> activityRule =
            new ActivityScenarioRule<>(LoginSignupActivity.class);

    @Before
    public void setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
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

    public void testEditPasswordFlow() throws Exception {
        // Login
        onView(withId(R.id.emailText))
                .perform(typeText("calebs62@iastate.edu"), closeSoftKeyboard());
        Thread.sleep(300);

        onView(withId(R.id.passwordText))
                .perform(typeText("123456"), closeSoftKeyboard());
        Thread.sleep(300);

        onView(withId(R.id.btnExplore))
                .perform(click());
        Thread.sleep(2000);

        // Navigate to ProfileFragment
        onView(withId(R.id.nav_profile)).perform(ViewActions.click());
        Thread.sleep(1000); // Wait for the ProfileFragment to load

        // Navigate to PasswordAndSecurityFragment
        onView(withId(R.id.passwordSecurity)).perform(ViewActions.click());
        Thread.sleep(1000); // Wait for PasswordAndSecurityFragment to load

        // Navigate to EditPasswordFragment
        onView(withId(R.id.passwordSecurity)).perform(ViewActions.click());
        Thread.sleep(1000); // Wait for EditPasswordFragment to load

        // Test mismatched passwords
        onView(withId(R.id.oldPasswordText)).perform(ViewActions.typeText("password123"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.newPasswordText)).perform(ViewActions.typeText("newpassword123"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.newPasswordTextConfirm)).perform(ViewActions.typeText("mismatch123"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.btnSave)).perform(ViewActions.click());
        Thread.sleep(500); // Wait for error to show

        // Check for password mismatch error
        onView(withId(R.id.tvPasswordMismatch)).check(matches(isDisplayed()));

        // Test incorrect old password
        onView(withId(R.id.newPasswordTextConfirm)).perform(ViewActions.clearText(), ViewActions.typeText("newpassword123"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.oldPasswordText)).perform(ViewActions.clearText(), ViewActions.typeText("wrongpassword"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.btnSave)).perform(ViewActions.click());
        Thread.sleep(500); // Wait for error to show

        // Check for invalid password error
        onView(withId(R.id.tvInvalidPassword)).check(matches(isDisplayed()));

        // Test successful password update
        onView(withId(R.id.oldPasswordText)).perform(ViewActions.clearText(), ViewActions.typeText("password123"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.newPasswordText)).perform(ViewActions.clearText(), ViewActions.typeText("newpassword123"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.newPasswordTextConfirm)).perform(ViewActions.clearText(), ViewActions.typeText("newpassword123"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.btnSave)).perform(ViewActions.click());
        Thread.sleep(2000); // Wait for navigation
    }

    @Test
    public void testUserProfileInitialization() throws Exception {
        // Login
        onView(withId(R.id.emailText))
                .perform(typeText("johndo3@gmail.com"), closeSoftKeyboard());
        Thread.sleep(300);

        onView(withId(R.id.passwordText))
                .perform(typeText("123456"), closeSoftKeyboard());
        Thread.sleep(300);

        onView(withId(R.id.btnExplore))
                .perform(click());
        Thread.sleep(2000);

        // Test if the user greeting is displayed correctly
        onView(withId(R.id.tvGreeting))
                .check(matches(withText("Hello, John!")));  // Replace with actual name if needed

        // Test that the weight and height inputs are empty initially
        onView(withId(R.id.etWeight)).check(matches(withText("")));
        onView(withId(R.id.etHeight)).check(matches(withText("")));

        // Simulate user entering weight and height
        onView(withId(R.id.etWeight)).perform(typeText("180"), closeSoftKeyboard());
        onView(withId(R.id.etHeight)).perform(typeText("70"), closeSoftKeyboard());

        // Simulate clicking the confirm button
        onView(withId(R.id.btnConfirm)).perform(click());
        Thread.sleep(1000); // Wait for the UI to update

        // Check if the BMI value is displayed and within expected range
        onView(withId(R.id.tvBMIValue))
                .check(matches(withText("25.82 You are overweight.")));  // Adjust the value as per BMI calculation

        // Test the recommendation and group selection behavior
        onView(withId(R.id.btnLoseWeight)).perform(click());
    }

    @Test
    public void testForgotPasswordWorkflow() throws Exception {
        // Login screen: Click on the "Forgot Password" link
        onView(withId(R.id.tvForgotPassword)).perform(click());
        Thread.sleep(1000); // Wait for the Forgot Password screen to load

        // Check if the Forgot Password screen is displayed
        onView(withId(R.id.forgot_password_activity)).check(matches(isDisplayed()));

        // Verify initial form fields are empty
        onView(withId(R.id.emailText)).check(matches(withText("")));
        onView(withId(R.id.passText)).check(matches(withText("")));
        onView(withId(R.id.confText)).check(matches(withText("")));

        // Test empty form submission
        onView(withId(R.id.btnSubmit)).perform(click());
        Thread.sleep(500); // Wait for the error dialog to show

        // Check for the empty form error message
        onView(withText("Passwords do not match!"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withText("OK")).perform(click()); // Close the dialog

        // Test invalid email format
        onView(withId(R.id.emailText)).perform(typeText("invalidEmail"), closeSoftKeyboard());
        onView(withId(R.id.passText)).perform(typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.confText)).perform(typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.btnSubmit)).perform(click());
        Thread.sleep(500); // Wait for the error dialog to show

        // Check for the email format error message
        onView(withText("invalidEmail is not a valid email!"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withText("OK")).perform(click()); // Close the dialog

        // Test mismatched passwords
        onView(withId(R.id.emailText)).perform(clearText(), typeText("johndo3@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.passText)).perform(clearText(), typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.confText)).perform(clearText(), typeText("654321"), closeSoftKeyboard());
        onView(withId(R.id.btnSubmit)).perform(click());
        Thread.sleep(500); // Wait for the error dialog to show

        // Check for the mismatched passwords error message
        onView(withText("Passwords do not match!"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withText("OK")).perform(click()); // Close the dialog

        // Test valid submission
        onView(withId(R.id.passText)).perform(clearText(), typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.confText)).perform(clearText(), typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.btnSubmit)).perform(click());
        Thread.sleep(500); // Wait for UI to process

    }

    @Test
    public void testUserRegistration() throws Exception {
        // Login screen: Click on the "Register" button
        onView(withId(R.id.tvRegister)).perform(click());
        Thread.sleep(1000); // Wait for the Register screen to load

        // Check if the Register screen is displayed
        onView(withId(R.id.register_activity)).check(matches(isDisplayed()));

        // Verify initial form fields are empty
        onView(withId(R.id.emailText)).check(matches(withText("")));
        onView(withId(R.id.registerPasswordText)).check(matches(withText("")));
        onView(withId(R.id.registerPasswordConfirmText)).check(matches(withText("")));
        onView(withId(R.id.firstNameText)).check(matches(withText("")));
        onView(withId(R.id.lastNameText)).check(matches(withText("")));

        // Test empty form submission
        onView(withId(R.id.btnRegister)).perform(click());
        Thread.sleep(500); // Wait for the error to show

        // Check for the empty form error message
        onView(withId(R.id.tvFormEmptyError))
                .check(matches(isDisplayed()));

        // Test invalid email format
        onView(withId(R.id.emailText)).perform(typeText("invalidEmail"), closeSoftKeyboard());
        onView(withId(R.id.registerPasswordText)).perform(typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.registerPasswordConfirmText)).perform(typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.firstNameText)).perform(typeText("John"), closeSoftKeyboard());
        onView(withId(R.id.lastNameText)).perform(typeText("Doe"), closeSoftKeyboard());
        onView(withId(R.id.btnRegister)).perform(click());
        Thread.sleep(500); // Wait for the error to show

        // Check for invalid email error message
        onView(withId(R.id.tvInvalidEmailError))
                .check(matches(withText("invalidEmail is not a valid email!")))
                .check(matches(isDisplayed()));

        // Test password too short
        onView(withId(R.id.emailText)).perform(clearText(), typeText("johndoe@example.com"), closeSoftKeyboard());
        onView(withId(R.id.registerPasswordText)).perform(clearText(), typeText("123"), closeSoftKeyboard());
        onView(withId(R.id.registerPasswordConfirmText)).perform(clearText(), typeText("123"), closeSoftKeyboard());
        onView(withId(R.id.btnRegister)).perform(click());
        Thread.sleep(500); // Wait for the error to show

        // Check for password length error message
        onView(withId(R.id.tvPasswordLengthError))
                .check(matches(isDisplayed()));

        // Test mismatched passwords
        onView(withId(R.id.registerPasswordText)).perform(clearText(), typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.registerPasswordConfirmText)).perform(clearText(), typeText("654321"), closeSoftKeyboard());
        onView(withId(R.id.btnRegister)).perform(click());
        Thread.sleep(500); // Wait for the dialog to show

        // Check for the mismatched passwords error dialog
        onView(withText("Passwords do not match!"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withText("OK")).perform(click()); // Close the dialog

        // Test valid submission
        onView(withId(R.id.registerPasswordConfirmText)).perform(clearText(), typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.btnRegister)).perform(click());
        Thread.sleep(500); // Wait for the success toast
    }
}
