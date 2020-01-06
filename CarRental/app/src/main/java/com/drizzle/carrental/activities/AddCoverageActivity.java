package com.drizzle.carrental.activities;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.drizzle.carrental.R;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.models.Company;
import com.drizzle.carrental.models.Coverage;

import java.util.GregorianCalendar;

public class AddCoverageActivity extends Activity implements View.OnClickListener {


    static final int START_COVERAGE_ACTIVITY_REQUEST = 1;
    static final int RECORD_VEHICLE_ACTIVITY_REQUEST = 2;
    static final int RECORD_MILE_ACTIVITY_REQUEST = 3;

    /**
     * UI Control Handlers
     */
    private TextView captionStartCoverage;
    private ImageButton buttonStartCoverage;
    private TextView captionRecordCar;
    private ImageButton buttonRecordCar;
    private TextView captionRecordMile;
    private ImageButton buttonRecordMile;

    private Button buttonGotit;
    private ImageButton buttonBack;

    private ImageButton buttonEdit;

    private LinearLayout layoutCoverage;
    private LinearLayout layoutCoverageLine;
    private LinearLayout layoutCoverageContent;
    private LinearLayout layoutCoverageLineHidden;

    private LinearLayout layoutVideoVehicle;
    private LinearLayout layoutVideoVehicleLine;
    private LinearLayout layoutVideoVehicleContent;
    private LinearLayout layoutVideoVehicleLineHiden;

    private LinearLayout layoutVideoMile;
    private LinearLayout layoutVideoMileLine;
    private LinearLayout layoutVideoMileContent;
    private LinearLayout layoutVideoMileLineHidden;

    private ImageButton buttonDeleteCoverage;
    private ImageButton buttonDeleteVideoVehicle;
    private ImageButton buttonDeleteVideoMile;

    private Button buttonLocation;
    private Button buttonCompany;
    private Button buttonPeriod;

    private ImageButton buttonVideoVehicle;
    private ImageButton buttonVideoMile;


    /**
     * Models
     */
    private Coverage coverage;
    private String vehicleVideoURL;
    private String vehicleMileURL;

    /**
     * Temporary variables for state
     */
    public enum CoverageCurrentStep {

        NEW("NEW ", 0),
        COVERAGE_ADDED("COVERAGE_ADDED", 1),
        VIDEO_VEHICLE_ADDED("VIDEO_VEHICLE_ADDED", 2),
        COMPLETED("COMPLETED", 3);

        private String stringValue;
        private int intValue;

        private CoverageCurrentStep(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        public int getIntValue() {

            return intValue;
        }

        @Override
        public String toString() {
            return stringValue;
        }
    }

    private boolean isEditMode;
    private CoverageCurrentStep coverageCurrentStep = CoverageCurrentStep.NEW;

    /**
     * get control handlers by id and add listenres
     */
    private void getControlHandlersAndLinkActions() {

        buttonBack = findViewById(R.id.button_back);
        captionStartCoverage = (TextView) findViewById(R.id.caption_start_coverage);
        buttonStartCoverage = (ImageButton) findViewById(R.id.button_start_coverage);

        captionRecordCar = (TextView) findViewById(R.id.caption_record_car);
        buttonRecordCar = (ImageButton) findViewById(R.id.button_record_car);

        captionRecordMile = (TextView) findViewById(R.id.caption_record_mile);
        buttonRecordMile = (ImageButton) findViewById(R.id.button_record_mile);

        buttonGotit = (Button) findViewById(R.id.button_got_it);
        buttonEdit = (ImageButton) findViewById((R.id.button_edit));
        buttonDeleteCoverage = (ImageButton) findViewById(R.id.button_delete_coverage);
        buttonDeleteVideoVehicle = (ImageButton) findViewById(R.id.button_delete_video);
        buttonDeleteVideoMile = (ImageButton) findViewById((R.id.button_delete_miles));
        layoutCoverage = (LinearLayout) findViewById(R.id.layout_coverage);
        layoutCoverageLine = findViewById(R.id.layout_coverage_line);
        layoutCoverageContent = findViewById(R.id.layout_coverage_content);
        layoutCoverageLineHidden = findViewById(R.id.layout_coverage_line_hidden);

        layoutVideoVehicle = (LinearLayout) findViewById(R.id.layout_video_vehicle);
        layoutVideoVehicleLine = findViewById(R.id.layout_video_vehicle_line);
        layoutVideoVehicleContent = findViewById(R.id.layout_video_vehicle_content);
        layoutVideoVehicleLineHiden = findViewById(R.id.layout_video_vehicle_line_hidden);

        layoutVideoMile = findViewById(R.id.layout_video_mile);
        layoutVideoMileLine = findViewById(R.id.layout_video_mile_line);
        layoutVideoMileContent = findViewById(R.id.layout_video_mile_content);
        layoutVideoMileLineHidden = findViewById(R.id.layout_video_mile_line_hidden);

        buttonLocation = findViewById(R.id.button_location);
        buttonCompany = findViewById(R.id.button_company);
        buttonPeriod = findViewById(R.id.button_period);

        buttonVideoVehicle = (ImageButton) findViewById(R.id.button_video_vehicle);
        buttonVideoMile = (ImageButton) findViewById(R.id.button_video_mile);

        //bind listener
        captionStartCoverage.setOnClickListener(this);
        buttonStartCoverage.setOnClickListener(this);
        captionRecordCar.setOnClickListener(this);
        buttonRecordCar.setOnClickListener(this);
        captionRecordMile.setOnClickListener(this);
        buttonRecordMile.setOnClickListener(this);

        buttonEdit.setOnClickListener(this);
        buttonDeleteCoverage.setOnClickListener(this);
        buttonDeleteVideoVehicle.setOnClickListener(this);
        buttonDeleteVideoMile.setOnClickListener(this);

        buttonGotit.setOnClickListener(this);
        buttonBack.setOnClickListener(this);

    }

    /**
     * Init and fetch data from server
     */
    private void initVariables() {

        coverage = new Coverage();

        //fetch data from server
        Location location = new Location("CoverageLocation");
        location.setLatitude(35.6118677);
        location.setLongitude(139.6872165);

        Company company = new Company();
        company.setId((long) 1);
        company.setName("Budge Rental Car");
        company.setType("Airport");

        coverage.setLocation(location);
        coverage.setCompany(company);
        coverage.setLocationAddress("Queens, NY 11430, USA");
        coverage.setDateFrom(new GregorianCalendar(2019, 1, 1));
        coverage.setDateTo(new GregorianCalendar(2020, 1, 1));

        vehicleVideoURL = "http://i.imgur.com/DvpvklR.png";
        vehicleMileURL = "http://i.imgur.com/DvpvklR.png";

        isEditMode = false;

    }

    /**
     * Update View according to current state
     */
    private void updateView() {

        if (coverage == null) {
            //layoutCoverageContent.setVisibility(View.GONE);
            layoutCoverage.setVisibility(View.GONE);
            layoutCoverageLineHidden.setVisibility(View.VISIBLE);


        } else {
            layoutCoverageLineHidden.setVisibility(View.GONE);
            layoutCoverage.setVisibility(View.VISIBLE);

            buttonLocation.setText(coverage.getLocationAddress());
            buttonPeriod.setText(coverage.getPeriod());
            if (coverage.getCompany() != null) {
                buttonCompany.setText(coverage.getCompany().getName());
            }
        }

        if (vehicleVideoURL == null || vehicleVideoURL.isEmpty()) {
            layoutVideoVehicleLineHiden.setVisibility(View.VISIBLE);
            layoutVideoVehicle.setVisibility(View.GONE);

        } else {
            layoutVideoVehicleLineHiden.setVisibility(View.GONE);
            layoutVideoVehicle.setVisibility(View.VISIBLE);
            buttonVideoVehicle.setImageResource(R.drawable.video_vehicle);
        }

        if (vehicleMileURL == null || vehicleMileURL.isEmpty()) {
            layoutVideoMileLineHidden.setVisibility(View.VISIBLE);
            layoutVideoMile.setVisibility(View.GONE);

        } else {
            layoutVideoMileLineHidden.setVisibility(View.GONE);
            layoutVideoMile.setVisibility(View.VISIBLE);
            buttonVideoMile.setImageResource(R.drawable.video_mile);
        }

        if (isEditMode) {
            buttonEdit.setVisibility(View.VISIBLE);
            buttonDeleteCoverage.setVisibility(View.VISIBLE);
            buttonDeleteVideoVehicle.setVisibility(View.VISIBLE);
            buttonDeleteVideoMile.setVisibility(View.VISIBLE);

        } else {
            buttonEdit.setVisibility(View.GONE);
            buttonDeleteCoverage.setVisibility(View.GONE);
            buttonDeleteVideoVehicle.setVisibility(View.GONE);
            buttonDeleteVideoMile.setVisibility(View.GONE);
        }

        if (coverageCurrentStep.equals(CoverageCurrentStep.NEW)) {

            captionStartCoverage.setBackgroundResource(R.drawable.coverage_add_button);
            captionRecordCar.setBackgroundResource(0);
            captionRecordMile.setBackgroundResource(0);
        }
        else if (coverageCurrentStep.equals(CoverageCurrentStep.COVERAGE_ADDED)) {

            captionStartCoverage.setBackgroundResource(0);
            captionRecordCar.setBackgroundResource(R.drawable.coverage_add_button);
            captionRecordMile.setBackgroundResource(0);
        }
        else if (coverageCurrentStep.equals(CoverageCurrentStep.VIDEO_VEHICLE_ADDED)) {

            captionStartCoverage.setBackgroundResource(0);
            captionRecordCar.setBackgroundResource(0);
            captionRecordMile.setBackgroundResource(R.drawable.coverage_add_button);
        }
        else {
            captionStartCoverage.setBackgroundResource(0);
            captionRecordCar.setBackgroundResource(0);
            captionRecordMile.setBackgroundResource(0);
        }
    }

    /**
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coverage);

        getControlHandlersAndLinkActions();

        //initVariables();

        updateView();


    }

    /**
     * OnClick Handlers
     *
     * @param view
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.caption_start_coverage:
            case R.id.button_start_coverage:

                navigateToStartCoveragePage();
                break;

            case R.id.caption_record_car:
            case R.id.button_record_car:

                navigateToRecordVehicleActivity();
                break;

            case R.id.caption_record_mile:
            case R.id.button_record_mile:

                navigateToRecordMileActivity();
                break;

            case R.id.button_back:
            case R.id.button_got_it:
                setResult(RESULT_OK);
                super.onBackPressed();
        }
    }


    /**
     * navigate to add new coverage activity
     */
    private void navigateToStartCoveragePage() {

        Intent intent = new Intent(AddCoverageActivity.this, StartCoverageActivity.class);
        startActivityForResult(intent, START_COVERAGE_ACTIVITY_REQUEST);
    }

    /**
     * navigate to record vehicle activity
     */
    private void navigateToRecordVehicleActivity() {

        Intent intent = new Intent(AddCoverageActivity.this, RecordVehicleActivity.class);
        startActivityForResult(intent, RECORD_VEHICLE_ACTIVITY_REQUEST);
    }

    /**
     * navigate to record vehicle activity
     */
    private void navigateToRecordMileActivity() {

        Intent intent = new Intent(AddCoverageActivity.this, RecordMileActivity.class);
        startActivityForResult(intent, RECORD_MILE_ACTIVITY_REQUEST);
    }

    private void completeAddCoverage() {

        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == START_COVERAGE_ACTIVITY_REQUEST) {

            if (resultCode == RESULT_OK) {
                coverage = Globals.coverage;
                coverageCurrentStep = CoverageCurrentStep.COVERAGE_ADDED;
                updateView();
            }
        }
        else if (requestCode == RECORD_VEHICLE_ACTIVITY_REQUEST) {

            if (resultCode == RESULT_OK) {
                vehicleVideoURL = Globals.coverage.getUrlVehicle();
                vehicleVideoURL = "aaa";
                        coverageCurrentStep = CoverageCurrentStep.VIDEO_VEHICLE_ADDED;
                updateView();
            }
        }
        else if (requestCode == RECORD_MILE_ACTIVITY_REQUEST) {

            if (resultCode == RESULT_OK) {
                vehicleMileURL = Globals.coverage.getUrlMile();
                vehicleMileURL  = "aaa";
                coverageCurrentStep = CoverageCurrentStep.COMPLETED;
                updateView();
            }
        }
    }

    @Override
    public void onBackPressed() {

        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
