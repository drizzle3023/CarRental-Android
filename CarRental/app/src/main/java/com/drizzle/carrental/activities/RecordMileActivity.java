package com.drizzle.carrental.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.drizzle.carrental.R;
import com.drizzle.carrental.globals.Constants;

public class RecordMileActivity extends Activity implements View.OnClickListener {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 88888;

    public static final int MY_CAMERA_ACTIVITY_REQUEST_CODE = 1;

    private boolean cameraPermission = false;
    /**
     * UI Control Handlers
     *
     * @param savedInstanceState
     */
    private Button buttonStart;
    private ImageButton buttonBack;

    private void getControlHandlersAndLinkActions() {

        buttonStart = (Button) findViewById(R.id.button_start);
        buttonBack = (ImageButton) findViewById(R.id.button_back_to_onboarding);

        buttonStart.setOnClickListener(this);
        buttonBack.setOnClickListener(this);

    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        // request camera permission if it has not been grunted.
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQUEST_CODE);
            return false;
        }
        cameraPermission = true;
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(RecordMileActivity.this, "camera permission has been grunted.", Toast.LENGTH_SHORT).show();
                    cameraPermission = true;
                } else {
                    Toast.makeText(RecordMileActivity.this, "[WARN] camera permission is not grunted.", Toast.LENGTH_SHORT).show();
                    cameraPermission = false;
                }
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_mile);

        getControlHandlersAndLinkActions();

        checkPermission();
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button_start) {

            if (cameraPermission) {
                Constants.isRecordingVehicleOrMileOrDamagedPart = 2;
                Intent intent = new Intent(RecordMileActivity.this, MyCameraActivity.class);
                startActivityForResult(intent, MY_CAMERA_ACTIVITY_REQUEST_CODE);
            }
        }
        if (view.getId() == R.id.button_back_to_onboarding) {

            finish();
        }
    }

    @Override
    public void onBackPressed(){

        finish();
    }

    private void backToPrevious() {

        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MY_CAMERA_ACTIVITY_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {

                backToPrevious();
            }
            else {

                //remove recorded file
                //Utils.removeTemporaryFile(BaseCameraActivity.getVideoFilePath());
            }
        }
    }
}
