package com.example.a1_jubair_6_frontend.model_tests;

import static org.junit.Assert.*;

import com.example.a1_jubair_6_frontend.models.NotificationSettings;

import org.junit.Before;
import org.junit.Test;

public class NotificationSettingsTest {

    private NotificationSettings settings;

    @Before
    public void setUp() {
        settings = new NotificationSettings();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull("Default constructor should create non-null object", settings);
        assertFalse("Default time notification should be false",
                settings.isTimeNotification());
        assertFalse("Default system notification should be false",
                settings.isSystemNotification());
        assertFalse("Default SMS notification should be false",
                settings.isSmsNotification());
        assertFalse("Default email notification should be false",
                settings.isEmailNotification());
    }

    @Test
    public void testTimeNotificationGetterAndSetter() {
        // Test default value
        assertFalse("Initial time notification should be false",
                settings.isTimeNotification());

        // Test setting to true
        settings.setTimeNotification(true);
        assertTrue("Time notification should be set to true",
                settings.isTimeNotification());

        // Test setting back to false
        settings.setTimeNotification(false);
        assertFalse("Time notification should be set back to false",
                settings.isTimeNotification());
    }

    @Test
    public void testSystemNotificationGetterAndSetter() {
        // Test default value
        assertFalse("Initial system notification should be false",
                settings.isSystemNotification());

        // Test setting to true
        settings.setSystemNotification(true);
        assertTrue("System notification should be set to true",
                settings.isSystemNotification());

        // Test setting back to false
        settings.setSystemNotification(false);
        assertFalse("System notification should be set back to false",
                settings.isSystemNotification());
    }

    @Test
    public void testSmsNotificationGetterAndSetter() {
        // Test default value
        assertFalse("Initial SMS notification should be false",
                settings.isSmsNotification());

        // Test setting to true
        settings.setSmsNotification(true);
        assertTrue("SMS notification should be set to true",
                settings.isSmsNotification());

        // Test setting back to false
        settings.setSmsNotification(false);
        assertFalse("SMS notification should be set back to false",
                settings.isSmsNotification());
    }

    @Test
    public void testEmailNotificationGetterAndSetter() {
        // Test default value
        assertFalse("Initial email notification should be false",
                settings.isEmailNotification());

        // Test setting to true
        settings.setEmailNotification(true);
        assertTrue("Email notification should be set to true",
                settings.isEmailNotification());

        // Test setting back to false
        settings.setEmailNotification(false);
        assertFalse("Email notification should be set back to false",
                settings.isEmailNotification());
    }

    @Test
    public void testAllNotificationsEnabled() {
        // Enable all notifications
        settings.setTimeNotification(true);
        settings.setSystemNotification(true);
        settings.setSmsNotification(true);
        settings.setEmailNotification(true);

        // Verify all are enabled
        assertTrue("Time notification should be enabled", settings.isTimeNotification());
        assertTrue("System notification should be enabled", settings.isSystemNotification());
        assertTrue("SMS notification should be enabled", settings.isSmsNotification());
        assertTrue("Email notification should be enabled", settings.isEmailNotification());
    }

    @Test
    public void testAllNotificationsDisabled() {
        // First enable all
        settings.setTimeNotification(true);
        settings.setSystemNotification(true);
        settings.setSmsNotification(true);
        settings.setEmailNotification(true);

        // Then disable all
        settings.setTimeNotification(false);
        settings.setSystemNotification(false);
        settings.setSmsNotification(false);
        settings.setEmailNotification(false);

        // Verify all are disabled
        assertFalse("Time notification should be disabled", settings.isTimeNotification());
        assertFalse("System notification should be disabled", settings.isSystemNotification());
        assertFalse("SMS notification should be disabled", settings.isSmsNotification());
        assertFalse("Email notification should be disabled", settings.isEmailNotification());
    }

    @Test
    public void testMixedNotificationStates() {
        settings.setTimeNotification(true);
        settings.setSystemNotification(false);
        settings.setSmsNotification(true);
        settings.setEmailNotification(false);

        assertTrue("Time notification should be enabled", settings.isTimeNotification());
        assertFalse("System notification should be disabled", settings.isSystemNotification());
        assertTrue("SMS notification should be enabled", settings.isSmsNotification());
        assertFalse("Email notification should be disabled", settings.isEmailNotification());
    }

    @Test
    public void testRepeatedToggling() {
        // Toggle time notification multiple times
        settings.setTimeNotification(true);
        settings.setTimeNotification(false);
        settings.setTimeNotification(true);
        assertTrue("Time notification should be true after odd number of toggles",
                settings.isTimeNotification());

        // Toggle system notification multiple times
        settings.setSystemNotification(true);
        settings.setSystemNotification(true);
        settings.setSystemNotification(true);
        assertTrue("System notification should remain true after multiple true settings",
                settings.isSystemNotification());
    }
}
