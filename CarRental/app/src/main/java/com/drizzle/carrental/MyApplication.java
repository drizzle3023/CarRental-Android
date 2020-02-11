package com.drizzle.carrental;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.drizzle.carrental.BuildConfig;
import com.drizzle.carrental.activities.SplashActivity;
import com.drizzle.carrental.globals.Constants;
import com.drizzle.carrental.globals.SharedHelper;
import com.drizzle.carrental.globals.Utils;

import net.gotev.uploadservice.UploadServiceConfig;

import java.io.File;
import java.io.IOException;

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

        Utils.initHabitSDK(this);

        if ( isExternalStorageWritable() ) {



            File appDirectory = new File( getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() );

            File logDirectory = appDirectory;

            File logFile = new File( logDirectory, "logcat" + System.currentTimeMillis() + ".txt" );



            // create app folder

            if ( !appDirectory.exists() ) {

                appDirectory.mkdir();

            }



            // create log folder

            if ( !logDirectory.exists() ) {

                logDirectory.mkdir();

            }



            // clear the previous logcat and then write the new one to the file

            try {
                //Process process = Runtime.getRuntime().exec( "logcat -v");

                Process process = Runtime.getRuntime().exec( "logcat -f " + logFile);

            } catch (IOException e) {
                //Utils.appendLog(System.err.toString());
                e.printStackTrace();
            }


        } else if ( isExternalStorageReadable() ) {

            // only readable

        } else {

            // not accessible

        }

    }

    /* Checks if external storage is available for read and write */

    public boolean isExternalStorageWritable() {

        String state = Environment.getExternalStorageState();

        if ( Environment.MEDIA_MOUNTED.equals( state ) ) {

            return true;

        }

        return false;

    }



    /* Checks if external storage is available to at least read */

    public boolean isExternalStorageReadable() {

        String state = Environment.getExternalStorageState();

        if ( Environment.MEDIA_MOUNTED.equals( state ) ||

                Environment.MEDIA_MOUNTED_READ_ONLY.equals( state ) ) {

            return true;

        }

        return false;

    }

}
