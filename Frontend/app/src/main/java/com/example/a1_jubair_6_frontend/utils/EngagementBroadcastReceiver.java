package com.example.a1_jubair_6_frontend.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.a1_jubair_6_frontend.managers.EngagementNotificationManager;

public class EngagementBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        EngagementNotificationManager manager = new EngagementNotificationManager(context);
        manager.checkAndSendEngagementNotification();
        // Schedule next check
        manager.scheduleEngagementCheck();
    }
}
