package com.example.a1_jubair_6_frontend.managers;

import android.content.Context;
import android.content.SharedPreferences;

public class NotificationDataManager {
    private static final String PREF_NAME = "NotificationPreferences";
    private static final String KEY_SYSTEM_NOTIFICATIONS = "system_notifications";
    private static final String KEY_TIME_NOTIFICATIONS = "time_notifications";
    private static final String KEY_REMINDER_NOTIFICATIONS = "reminder_notifications";

    private final SharedPreferences preferences;
    private final Context context;
    private final ProfileDataManager profileDataManager;

    public NotificationDataManager(Context context) {
        this.context = context;
        this.profileDataManager = new ProfileDataManager(context);
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public ProfileDataManager getProfileDataManager() {
        return profileDataManager;
    }

    public void saveNotificationSettings(boolean systemEnabled, boolean timeEnabled, boolean reminderEnabled) {
        preferences.edit()
                .putBoolean(KEY_SYSTEM_NOTIFICATIONS, systemEnabled)
                .putBoolean(KEY_TIME_NOTIFICATIONS, timeEnabled)
                .putBoolean(KEY_REMINDER_NOTIFICATIONS, reminderEnabled)
                .apply();
    }

    public boolean getSystemNotificationsEnabled() {
        return preferences.getBoolean(KEY_SYSTEM_NOTIFICATIONS, true);
    }

    public boolean getTimeNotificationsEnabled() {
        return preferences.getBoolean(KEY_TIME_NOTIFICATIONS, true);
    }

    public boolean getReminderNotificationsEnabled() {
        return preferences.getBoolean(KEY_REMINDER_NOTIFICATIONS, true);
    }

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

    public void toggleTimeNotifications() {
        if (getSystemNotificationsEnabled()) {
            boolean current = getTimeNotificationsEnabled();
            preferences.edit()
                    .putBoolean(KEY_TIME_NOTIFICATIONS, !current)
                    .apply();
            updateNotificationSettingsToServer();
        }
    }

    public void toggleReminderNotifications() {
        if (getSystemNotificationsEnabled()) {
            boolean current = getReminderNotificationsEnabled();
            preferences.edit()
                    .putBoolean(KEY_REMINDER_NOTIFICATIONS, !current)
                    .apply();
            updateNotificationSettingsToServer();
        }
    }

    public void updateNotificationSettingsToServer() {

    }

    public void fetchNotificationSettings() {

    }

    public void clearNotificationSettings() {
        preferences.edit()
                .remove(KEY_SYSTEM_NOTIFICATIONS)
                .remove(KEY_TIME_NOTIFICATIONS)
                .remove(KEY_REMINDER_NOTIFICATIONS)
                .apply();
    }

}
