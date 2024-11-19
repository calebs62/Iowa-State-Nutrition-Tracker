package com.example.a1_jubair_6_frontend.managers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manages the notification settings for the application. It allows the user to configure and
 * toggle various types of notifications, including system notifications, time-based notifications,
 * reminder notifications, SMS notifications, and email notifications. Settings are stored in
 * SharedPreferences to persist across app sessions.
 *
 * @author Caleb Sanchez, Alexander Svobodny
 */
public class NotificationDataManager {

    private static final String PREF_NAME = "NotificationPreferences";
    private static final String KEY_SYSTEM_NOTIFICATIONS = "system_notifications";
    private static final String KEY_TIME_NOTIFICATIONS = "time_notifications";
    private static final String KEY_REMINDER_NOTIFICATIONS = "reminder_notifications";
    private static final String KEY_SMS_NOTIFICATIONS = "sms_notifications";
    private static final String KEY_EMAIL_NOTIFICATIONS = "email_notifications";

    private final SharedPreferences preferences;
    private final Context context;
    private final ProfileDataManager profileDataManager;

    /**
     * Initializes the NotificationDataManager instance with the provided context.
     *
     * @param context the context of the application, used to access SharedPreferences.
     */
    public NotificationDataManager(Context context) {
        this.context = context;
        this.profileDataManager = new ProfileDataManager(context);
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Retrieves the associated ProfileDataManager instance.
     *
     * @return the ProfileDataManager instance for managing user profile data.
     */
    public ProfileDataManager getProfileDataManager() {
        return profileDataManager;
    }

    /**
     * Saves the notification settings, including system, time, and reminder notifications.
     *
     * @param systemEnabled whether system notifications are enabled.
     * @param timeEnabled whether time notifications are enabled.
     * @param reminderEnabled whether reminder notifications are enabled.
     */
    public void saveNotificationSettings(boolean systemEnabled, boolean timeEnabled, boolean reminderEnabled) {
        preferences.edit()
                .putBoolean(KEY_SYSTEM_NOTIFICATIONS, systemEnabled)
                .putBoolean(KEY_TIME_NOTIFICATIONS, timeEnabled)
                .putBoolean(KEY_REMINDER_NOTIFICATIONS, reminderEnabled)
                .apply();
    }

    /**
     * Retrieves the current setting for system notifications.
     *
     * @return true if system notifications are enabled, false otherwise.
     */
    public boolean getSystemNotificationsEnabled() {
        return preferences.getBoolean(KEY_SYSTEM_NOTIFICATIONS, true);
    }

    /**
     * Retrieves the current setting for time-based notifications.
     *
     * @return true if time-based notifications are enabled, false otherwise.
     */
    public boolean getTimeNotificationsEnabled() {
        return preferences.getBoolean(KEY_TIME_NOTIFICATIONS, true);
    }

    /**
     * Retrieves the current setting for reminder notifications.
     *
     * @return true if reminder notifications are enabled, false otherwise.
     */
    public boolean getReminderNotificationsEnabled() {
        return preferences.getBoolean(KEY_REMINDER_NOTIFICATIONS, true);
    }

    /**
     * Retrieves the current setting for SMS notifications.
     *
     * @return true if SMS notifications are enabled, false otherwise.
     */
    public boolean getSMSNotificationsEnabled() {
        return preferences.getBoolean(KEY_SMS_NOTIFICATIONS, true);
    }

    /**
     * Retrieves the current setting for email notifications.
     *
     * @return true if email notifications are enabled, false otherwise.
     */
    public boolean getEmailNotificationsEnabled() {
        return preferences.getBoolean(KEY_EMAIL_NOTIFICATIONS, true);
    }

    /**
     * Toggles the state of system notifications, and disables time and reminder notifications if
     * system notifications are turned off.
     */
    public void toggleSystemNotifications() {
        boolean current = getSystemNotificationsEnabled();
        preferences.edit()
                .putBoolean(KEY_SYSTEM_NOTIFICATIONS, !current)
                .apply();

        // If system notifications are disabled, disable other notifications too
        if (current) {
            preferences.edit()
                    .putBoolean(KEY_TIME_NOTIFICATIONS, false)
                    .putBoolean(KEY_REMINDER_NOTIFICATIONS, false)
                    .apply();
        }

        updateNotificationSettingsToServer();
    }

    /**
     * Toggles the state of time notifications, ensuring that system notifications are enabled
     * before allowing time notifications to be toggled.
     */
    public void toggleTimeNotifications() {
        if (getSystemNotificationsEnabled()) {
            boolean current = getTimeNotificationsEnabled();
            preferences.edit()
                    .putBoolean(KEY_TIME_NOTIFICATIONS, !current)
                    .apply();
            updateNotificationSettingsToServer();
        }
    }

    /**
     * Toggles the state of reminder notifications, ensuring that system notifications are enabled
     * before allowing reminder notifications to be toggled.
     */
    public void toggleReminderNotifications() {
        if (getSystemNotificationsEnabled()) {
            boolean current = getReminderNotificationsEnabled();
            preferences.edit()
                    .putBoolean(KEY_REMINDER_NOTIFICATIONS, !current)
                    .apply();
            updateNotificationSettingsToServer();
        }
    }

    /**
     * Toggles the state of SMS notifications.
     */
    public void toggleSMSNotifications() {
        if (getSMSNotificationsEnabled()) {
            boolean current = getSMSNotificationsEnabled();
            preferences.edit()
                    .putBoolean(KEY_REMINDER_NOTIFICATIONS, !current)
                    .apply();
            updateNotificationSettingsToServer();
        }
    }

    /**
     * Toggles the state of email notifications.
     */
    public void toggleEmailNotifications() {
        if (getEmailNotificationsEnabled()) {
            boolean current = getEmailNotificationsEnabled();
            preferences.edit()
                    .putBoolean(KEY_EMAIL_NOTIFICATIONS, !current)
                    .apply();
            updateNotificationSettingsToServer();
        }
    }

    /**
     * Updates the notification settings to the server. This method is a placeholder and should
     * be implemented with server communication logic to synchronize the settings with the backend.
     */
    public void updateNotificationSettingsToServer() {
        // Placeholder for server update logic
    }

    /**
     * Fetches the notification settings from the server. This method is a placeholder and should
     * be implemented with server communication logic to fetch the settings from the backend.
     */
    public void fetchNotificationSettings() {
        // Placeholder for server fetch logic
    }

    /**
     * Clears all saved notification settings from SharedPreferences.
     */
    public void clearNotificationSettings() {
        preferences.edit()
                .remove(KEY_SYSTEM_NOTIFICATIONS)
                .remove(KEY_TIME_NOTIFICATIONS)
                .remove(KEY_REMINDER_NOTIFICATIONS)
                .apply();
    }
}
