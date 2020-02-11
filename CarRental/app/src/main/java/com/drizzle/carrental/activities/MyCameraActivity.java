package com.drizzle.carrental.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

import com.drizzle.carrental.R;

public class MyCameraActivity extends BaseCameraActivity {

    public static void startActivity(Activity activity) {

        Intent intent = new Intent(activity, MyCameraActivity.class);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_recording);


        onCreateActivity();

        videoWidth = 720;
        videoHeight = 1280;
        cameraWidth = 1280;
        cameraHeight = 720;
    }

}

