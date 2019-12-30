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

    public static final int RESULT_CODE_BACK = 1000001;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 88888;

    /**
     * UI Control Handlers
     *
     * @param savedInstanceState
     */
    private Button buttonStart;
    private ImageButton buttonBack;

    private void getControlHandlersAndLinkActions() {

        buttonStart = (Button) findViewById(R.id.button_start);
        buttonBack = (ImageButton) findViewById(R.id.button_back);

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

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(RecordMileActivity.this, "camera permission has been grunted.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RecordMileActivity.this, "[WARN] camera permission is not grunted.", Toast.LENGTH_SHORT).show();
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

            Constants.isRecordingVehicleOrMile = false;
            Intent intent = new Intent(RecordMileActivity.this, MyCameraActivity.class);
            startActivity(intent);
        }
        if (view.getId() == R.id.button_back) {

            backToPrevious();
        }
    }

    @Override
    public void onBackPressed(){

        backToPrevious();
    }

    private void backToPrevious() {

        setResult(RESULT_CODE_BACK);
        super.onBackPressed();
    }
}
