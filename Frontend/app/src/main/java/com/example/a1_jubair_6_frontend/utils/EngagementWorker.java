package com.example.a1_jubair_6_frontend.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.a1_jubair_6_frontend.managers.EngagementNotificationManager;

public class EngagementWorker extends Worker {
    private final EngagementNotificationManager notificationManager;

    public EngagementWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        notificationManager = new EngagementNotificationManager(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        notificationManager.checkAndSendEngagementNotification();
        return Result.success();
    }
}
