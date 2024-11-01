package com.example.a1_jubair_6_frontend.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;

import com.example.a1_jubair_6_frontend.managers.ProfileDataManager;
import com.example.a1_jubair_6_frontend.models.Notification;
import com.example.a1_jubair_6_frontend.models.NotificationSettings;

import java.util.ArrayList;

public class NotificationSender {
    private Context context;
    private static final String TAG = "NotificationSender";

    public static final String TYPE_SYSTEM = "SYSTEM";
    public static final String TYPE_TIME = "TIME";
    public static final String TYPE_SMS = "SMS";
    public static final String TYPE_EMAIL = "EMAIL";

    private ProfileDataManager profileDataManager;

    public NotificationSender(Context context) {
        this.context = context;
        profileDataManager = new ProfileDataManager(context);
    }

    public void handleNotification(Notification notification, NotificationSettings settings) {
        switch (notification.getType()) {
            case TYPE_SMS:
                if (settings.isSmsNotification()) {
                    sendSMS(notification);
                }
                break;
            case TYPE_EMAIL:
                if (settings.isEmailNotification()) {
                    sendEmail(notification);
                }
                break;
        }
    }

    private void sendSMS(Notification notification) {
        String phoneNumber = profileDataManager.getPhoneNumber();
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            Log.e(TAG, "No phone number available for SMS");
            return;
        }

        try {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(notification.getBody());
            smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
            Log.d(TAG, "SMS sent successfully: " + notification.getHeader());
        } catch (Exception e) {
            Log.e(TAG, "Failed to send SMS: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendEmail(Notification notification) {
        String email = profileDataManager.getEmail();
        if (email == null || email.isEmpty()) {
            Log.e(TAG, "No email address available");
            return;
        }

        try {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, notification.getHeader());
            emailIntent.putExtra(Intent.EXTRA_TEXT, notification.getBody());
            emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (emailIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(emailIntent);
                Log.d(TAG, "Email intent launched successfully");
            } else {
                Log.e(TAG, "No email app available");
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to launch email intent: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
