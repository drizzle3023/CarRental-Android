package com.drizzle.carrental.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.drizzle.carrental.R;
import com.drizzle.carrental.services.GetAddressIntentService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationResult;

import android.location.Location;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class StartCoverageActivity extends AppCompatActivity implements View.OnClickListener {


    /**
     * UI Control Handlers
     */
    private ImageButton buttonBack;

    private FrameLayout layoutPickUpLocation;
    private FrameLayout layoutRentalCompany;
    private FrameLayout layoutStartDate;
    private FrameLayout layoutEndDate;

    private TextView textViewPickUpLocation;
    private TextView textViewCompany;
    private TextView textViewStartDate;
    private TextView textViewEndDate;

    private Button doneButton;

    /**
     * Vars for location address
     */
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private LocationAddressResultReceiver addressResultReceiver;
    private Location currentLocation;
    private LocationCallback locationCallback;


    /**
     * Get UI Control Handlers and link events
     */
    private void getControlHandlersAndLinkActions() {

        buttonBack = (ImageButton) findViewById(R.id.button_back);

        layoutPickUpLocation = (FrameLayout) findViewById(R.id.layout_pickup_location);
        layoutRentalCompany = (FrameLayout) findViewById(R.id.layout_rental_company);
        layoutStartDate = (FrameLayout) findViewById(R.id.layout_start_date);
        layoutEndDate = (FrameLayout) findViewById(R.id.layout_dropoff_date);

        doneButton = (Button) findViewById(R.id.button_done);

        buttonBack.setOnClickListener(this);
        layoutPickUpLocation.setOnClickListener(this);
        layoutRentalCompany.setOnClickListener(this);
        layoutStartDate.setOnClickListener(this);
        layoutEndDate.setOnClickListener(this);

        textViewPickUpLocation = (TextView) findViewById(R.id.textview_pickup_location);
        textViewCompany = (TextView) findViewById(R.id.textview_rental_company);
        textViewStartDate = (TextView) findViewById(R.id.textview_start_date);
        textViewEndDate = (TextView) findViewById(R.id.textview_end_date);


    }

    private void getCurrentAddress() {

        addressResultReceiver = new LocationAddressResultReceiver(new Handler());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                currentLocation = locationResult.getLocations().get(0);
                getAddress();
                fusedLocationClient.removeLocationUpdates(locationCallback);
            };
        };
        startLocationUpdates();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_coverage);

        getControlHandlersAndLinkActions();

    }



    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button_back) {

            finish();
        }
        if (view.getId() == R.id.button_done) {

            Intent intent = new Intent(StartCoverageActivity.this, RecordVehicleActivity.class);
            startActivity(intent);

        }
        if (view.getId() == R.id.layout_pickup_location) {

            getCurrentAddress();
        }

    }

    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(2000);
            locationRequest.setFastestInterval(1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    null);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getAddress() {

        if (!Geocoder.isPresent()) {
            Toast.makeText(this,
                    "Can't find current address, ",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, GetAddressIntentService.class);
        intent.putExtra("add_receiver", addressResultReceiver);
        intent.putExtra("add_location", currentLocation);
        startService(intent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                } else {
                    Toast.makeText(this, "Location permission not granted, " +
                                    "restart the app if you want the feature",
                            Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }

    private class LocationAddressResultReceiver extends ResultReceiver {
        LocationAddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultCode == 0) {
                //Last Location can be null for various reasons
                //for example the api is called first time
                //so retry till location is set
                //since intent service runs on background thread, it doesn't block main thread
                Log.d("Address", "Location null retrying");
                getAddress();
            }

            if (resultCode == 1) {

            }

            String currentAdd = resultData.getString("address_result");

            showResults(currentAdd);
        }
    }

    private void showResults(String currentAdd) {

        textViewPickUpLocation.setText(currentAdd);
    }


    @Override
    protected void onResume() {

        super.onResume();

    }

    @Override
    protected void onPause() {

        super.onPause();

    }

}
