package com.example.a1_jubair_6_frontend.managers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.utils.EngagementWorker;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Manages user engagement notifications for the application.
 * This class handles the scheduling, creation, and sending of notifications
 * to encourage user engagement based on their last access time.
 * It supports different message types for various periods of inactivity
 * (daily, weekly, and monthly).
 *
 * @author Alexander Svobodny, Caleb Sanchez
 */
public class EngagementNotificationManager {
    /** Shared preferences file name for storing engagement data */
    private static final String PREF_NAME = "EngagementPreferences";

    /** Key for storing the last access timestamp */
    private static final String KEY_LAST_ACCESS = "last_access_time";

    /** Notification channel ID for engagement notifications */
    private static final String CHANNEL_ID = "engagement_channel";

    /** Unique identifier for engagement notifications */
    private static final int NOTIFICATION_ID = 1001;

    /** Tag for WorkManager engagement check tasks */
    private static final String WORK_TAG = "engagement_check";

    /** Application context */
    private final Context context;

    /** SharedPreferences instance for storing engagement data */
    private final SharedPreferences preferences;

    /** Random number generator for message selection */
    private final Random random;

    /** Array of notification messages for daily engagement */
    private final String[] dayMessages = {
            "We miss you! Come check your calories for today! 📱",
            "Don't break your streak! Log your meals today 🍽️",
            "One day without tracking? Let's get back on track! 💪",
            "Your health journey is waiting! Come back and log your progress 🎯"
    };

    /** Array of notification messages for weekly engagement */
    private final String[] weekMessages = {
            "It's been a week! Don't forget about your health goals 🎯",
            "A week goes by fast! Let's resume your progress 📈",
            "Missing your food tracking routine? We're here to help! 🍎",
            "One week without updates - come back and stay on track! 💪"
    };

    /** Array of notification messages for monthly engagement */
    private final String[] monthMessages = {
            "A month is too long! Let's restart your health journey 🌟",
            "New month, new goals! Return and set your targets 🎯",
            "Your health matters! Come back and track your progress 💪",
            "Missing your progress updates? Let's get back to it! 📱"
    };

    /**
     * Constructs a new EngagementNotificationManager.
     * Initializes the notification channel and schedules periodic engagement checks.
     *
     * @param context The application context used for accessing system services
     */
    public EngagementNotificationManager(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.random = new Random();
        createNotificationChannel();
        scheduleEngagementCheck();
    }

    /**
     * Updates the timestamp of the user's last access to the application.
     * This timestamp is used to calculate periods of inactivity.
     */
    public void updateLastAccessTime() {
        preferences.edit()
                .putLong(KEY_LAST_ACCESS, System.currentTimeMillis())
                .apply();
    }

    /**
     * Creates a notification channel for engagement notifications on Android O and above.
     * This is required for displaying notifications on newer Android versions.
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Engagement Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications to encourage app engagement");

            NotificationManager notificationManager =
                    context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Schedules periodic checks for user engagement using WorkManager.
     * Sets up a periodic work request that runs every 24 hours to check
     * user activity and send notifications if needed.
     */
    public void scheduleEngagementCheck() {
        // Create a periodic work request that runs every 24 hours
        PeriodicWorkRequest engagementWorkRequest =
                new PeriodicWorkRequest.Builder(EngagementWorker.class,
                        24, TimeUnit.HOURS)
                        .addTag(WORK_TAG)
                        .build();

        // queue the work request
        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                        WORK_TAG,
                        ExistingPeriodicWorkPolicy.KEEP,
                        engagementWorkRequest
                );
    }

    /**
     * Checks the user's last access time and sends an appropriate notification
     * if the user has been inactive for a significant period.
     * Different messages are selected based on the duration of inactivity:
     * <ul>
     *     <li>30+ days: Monthly messages</li>
     *     <li>7+ days: Weekly messages</li>
     *     <li>1+ days: Daily messages</li>
     * </ul>
     */
    public void checkAndSendEngagementNotification() {
        long lastAccess = preferences.getLong(KEY_LAST_ACCESS, 0);
        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime - lastAccess;

        long daysElapsed = timeDiff / (1000 * 60 * 60 * 24);

        String message;
        String title;

        if (daysElapsed >= 30) {
            title = "We Really Miss You!";
            message = monthMessages[random.nextInt(monthMessages.length)];
        } else if (daysElapsed >= 7) {
            title = "It's Been a While!";
            message = weekMessages[random.nextInt(weekMessages.length)];
        } else if (daysElapsed >= 1) {
            title = "Come Back!";
            message = dayMessages[random.nextInt(dayMessages.length)];
        } else {
            return;
        }

        sendNotification(title, message);
    }

    /**
     * Sends a notification with the specified title and message.
     * Handles the actual creation and display of the notification using
     * the NotificationCompat.Builder.
     *
     * @param title The title to display in the notification
     * @param message The message body to display in the notification
     */
    private void sendNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notifications_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        try {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}
