package com.drizzle.carrental;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.drizzle.carrental.BuildConfig;
import com.drizzle.carrental.activities.SplashActivity;
import com.drizzle.carrental.globals.Constants;
import com.drizzle.carrental.globals.SharedHelper;

import net.gotev.uploadservice.UploadServiceConfig;

import io.habit.analytics.HabitStatusCodes;
import io.habit.analytics.SDK;
import kotlin.Unit;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.functions.Function1;
import lombok.val;

public class MyApplication extends Application {


    final String notificationChannelId = "TestChannel";

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(notificationChannelId, "TestApp Channel", NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //init upload service sdk
        createNotificationChannel();

        UploadServiceConfig.initialize(getPackageName(),
                notificationChannelId,
                BuildConfig.DEBUG);

    }
}
