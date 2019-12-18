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

import com.drizzle.carrental.R;
import com.drizzle.carrental.models.Company;
import com.drizzle.carrental.models.Coverage;
import com.squareup.picasso.Picasso;

import java.util.GregorianCalendar;

public class AddCoverageActivity extends Activity implements View.OnClickListener {

    /**
     * UI Control Handlers
     */
    private TextView captionStartCoverage;
    private ImageButton buttonStartCoverage;
    private TextView captionRecordCar;
    private ImageButton buttonRecordCar;
    private TextView captionRecordMile;
    private ImageButton buttonRecordMile;

    private Button buttonAdd;

    private ImageButton buttonEdit;

    private LinearLayout layoutCoverage;
    private LinearLayout layoutVideoVehicle;
    private LinearLayout layoutVideoMile;

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
    private boolean isEditMode;


    /**
     * get control handlers by id and add listenres
     */
    private void getControlHandlersAndLinkActions() {

        captionStartCoverage = (TextView) findViewById(R.id.caption_start_coverage);
        buttonStartCoverage = (ImageButton) findViewById(R.id.button_start_coverage);

        captionRecordCar = (TextView) findViewById(R.id.caption_record_car);
        buttonRecordCar = (ImageButton) findViewById(R.id.button_record_car);

        captionRecordMile = (TextView) findViewById(R.id.caption_record_mile);
        buttonRecordMile = (ImageButton) findViewById(R.id.button_record_mile);

        buttonAdd = (Button) findViewById(R.id.button_add);
        buttonEdit = (ImageButton) findViewById((R.id.button_edit));
        buttonDeleteCoverage = (ImageButton) findViewById(R.id.button_delete_coverage);
        buttonDeleteVideoVehicle = (ImageButton) findViewById(R.id.button_delete_video);
        buttonDeleteVideoMile = (ImageButton) findViewById((R.id.button_delete_miles));
        layoutCoverage = (LinearLayout) findViewById(R.id.layout_coverage);
        layoutVideoVehicle = (LinearLayout) findViewById(R.id.layout_video_vehicle);
        layoutVideoMile = (LinearLayout) findViewById(R.id.layout_video_mile);

        buttonLocation = (Button) findViewById(R.id.button_location);
        buttonCompany = (Button) findViewById(R.id.button_company);
        buttonPeriod = (Button) findViewById(R.id.button_period);

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
        company.setId((long)1);
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
            layoutCoverage.setVisibility(View.GONE);
        } else {
            buttonLocation.setText(coverage.getLocationAddress());
            buttonPeriod.setText(coverage.getPeriod());
            if (coverage.getCompany() != null) {
                buttonCompany.setText(coverage.getCompany().getName());
            }
        }

        if (vehicleVideoURL == null || vehicleVideoURL.isEmpty()) {
            layoutVideoVehicle.setVisibility(View.GONE);
        } else {
            //Picasso.get().load(vehicleVideoURL).placeholder(R.drawable.video_thumbnails).into(buttonVideoVehicle);
        }

        if (vehicleMileURL == null || vehicleMileURL.isEmpty()) {
            layoutVideoMile.setVisibility(View.GONE);
        } else {
            //Picasso.get().load(vehicleMileURL).placeholder(R.drawable.video_thumbnails).into(buttonVideoMile);

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

    }

    /**
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coverage);

        getControlHandlersAndLinkActions();

        initVariables();

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

                navigateToAddCoveragePage();

                break;

            case R.id.caption_record_car:
            case R.id.button_record_car:
                break;

            case R.id.caption_record_mile:
            case R.id.button_record_mile:
                break;

        }
    }

    private void navigateToAddCoveragePage() {

        Intent intent = new Intent(AddCoverageActivity.this, HomeActivity.class);
        startActivity(intent);

    }
}
