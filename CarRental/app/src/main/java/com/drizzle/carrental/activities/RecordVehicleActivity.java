package com.drizzle.carrental.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;

import com.drizzle.carrental.R;

public class RecordVehicleActivity extends Activity implements View.OnClickListener {

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_car);

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button_start) {

        }
        if (view.getId() == R.id.button_back) {
            finish();
        }

    }
}
