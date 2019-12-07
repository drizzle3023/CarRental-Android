package com.drizzle.carrental.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.drizzle.carrental.R;

public class AddCoverageActivity extends Activity implements View.OnClickListener {

    private TextView captionStartCoverage;
    private ImageButton buttonStartCoverage;

    private TextView captionRecordCar;
    private ImageButton buttonRecordCar;

    private TextView captionRecordMile;
    private ImageButton buttonRecordMile;

    private Button buttonAdd;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coverage);

        captionStartCoverage = (TextView) findViewById(R.id.caption_start_coverage);
        buttonStartCoverage = (ImageButton) findViewById(R.id.button_start_coverage);

        captionRecordCar = (TextView) findViewById(R.id.caption_record_car);
        buttonRecordCar = (ImageButton) findViewById(R.id.button_record_car);

        captionRecordMile = (TextView) findViewById(R.id.caption_record_mile);
        buttonRecordMile = (ImageButton) findViewById(R.id.button_record_mile);

        buttonAdd = (Button) findViewById(R.id.button_add);


        //bind listener
        captionStartCoverage.setOnClickListener(this);
        buttonStartCoverage.setOnClickListener(this);
        captionRecordCar.setOnClickListener(this);
        buttonRecordCar.setOnClickListener(this);
        captionRecordMile.setOnClickListener(this);
        buttonRecordMile.setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.caption_start_coverage:
            case R.id.button_start_coverage:

                break;

            case R.id.caption_record_car:
            case R.id.button_record_car:
                break;

            case R.id.caption_record_mile:
            case R.id.button_record_mile:
                break;

        }
    }
}
