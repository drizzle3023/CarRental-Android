package com.drizzle.carrental.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.drizzle.carrental.R;
import com.drizzle.carrental.adapters.CustomAdapterCompanySelect;
import com.drizzle.carrental.api.ApiClient;
import com.drizzle.carrental.api.ApiInterface;
import com.drizzle.carrental.enumerators.CoverageState;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.globals.SharedHelper;
import com.drizzle.carrental.models.Company;
import com.drizzle.carrental.models.VehicleType;
import com.drizzle.carrental.services.GetAddressIntentService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import android.location.Location;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartCoverageActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, Callback<ResponseBody> {

    /**
     * UI Control Handlers
     */

    ProgressDialog progressDialog;

    private ImageButton buttonBack;

    private FrameLayout layoutPickUpLocation;
    private RelativeLayout layoutRentalCompany;
    private FrameLayout layoutStartDate;
    private FrameLayout layoutEndDate;

    private TextView textViewPickUpLocation;
    private TextView textViewCompany;
    private TextView textViewStartDate;
    private TextView textViewEndDate;

    private Button buttonDone;
    private Spinner spinnerCompanySelector;
    private ImageView buttonDropDown;

    private ArrayList<Company> companies;

    private Company selectedCompany;

    /**
     * Vars for location address
     */
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private LocationAddressResultReceiver addressResultReceiver;
    private Location currentLocation;
    private LocationCallback locationCallback;

    private boolean isGettingCompanyListOrSubitAction = true;
    /**
     * Get UI Control Handlers and link events
     */
    private void getControlHandlersAndLinkActions() {

        buttonBack = (ImageButton) findViewById(R.id.button_back);

        layoutPickUpLocation = (FrameLayout) findViewById(R.id.layout_pickup_location);
        layoutRentalCompany = findViewById(R.id.layout_rental_company);
        layoutStartDate = (FrameLayout) findViewById(R.id.layout_start_date);
        layoutEndDate = (FrameLayout) findViewById(R.id.layout_dropoff_date);

        spinnerCompanySelector = findViewById(R.id.spinner_company_select);
        buttonDropDown = findViewById(R.id.company_select_dropdown_button);

        buttonDone = (Button) findViewById(R.id.button_done);

        buttonBack.setOnClickListener(this);
        layoutPickUpLocation.setOnClickListener(this);
        layoutRentalCompany.setOnClickListener(this);
        layoutStartDate.setOnClickListener(this);
        layoutEndDate.setOnClickListener(this);
        buttonDone.setOnClickListener(this);

        //spinner = (Spinner) findViewById(R.id.compan);
        spinnerCompanySelector.setOnItemSelectedListener(this);

        textViewPickUpLocation = (TextView) findViewById(R.id.textview_pickup_location);
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

                isGettingCompanyListOrSubitAction = true;

                fetchCompaniesFromServer();

                Globals.coverage.setLocation(currentLocation);
                String strAddress = getAddressFromLocation();
                Globals.coverage.setLocationAddress(strAddress);
                textViewPickUpLocation.setText(strAddress);

                //getAddress();
                fusedLocationClient.removeLocationUpdates(locationCallback);
            }

            ;
        };
        startLocationUpdates();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_coverage);

        companies = new ArrayList<>();

        progressDialog = new ProgressDialog(this);

        getControlHandlersAndLinkActions();

        getCurrentAddress();


    }

    private void fetchCompaniesFromServer() {

        //prepare restrofit2 request parameters
        JsonObject gSonObject = new JsonObject();

        //set parameters using org.JSONObject
        JSONObject paramObject = new JSONObject();
        try {

            paramObject.put("access_token", SharedHelper.getKey(this, "access_token"));
            paramObject.put("latitude", currentLocation.getLatitude());
            paramObject.put("longitude", currentLocation.getLongitude());

        } catch (JSONException e) {

            e.printStackTrace();
        }

        JsonParser jsonParser = new JsonParser();
        gSonObject = (JsonObject) jsonParser.parse(paramObject.toString());

        //get apiInterface
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        //display waiting dialog
        showWaitingScreen();
        //send request
        apiInterface.getNearCompanyList(gSonObject).enqueue(this);
    }

    private void showWaitingScreen() {

        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideWaitingScreen() {

        progressDialog.dismiss();
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

        hideWaitingScreen();

        String responseString = null;
        try {
            ResponseBody body = response.body();
            if (body != null) {
                responseString = body.string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject object = null;
        if (responseString != null) {
            try {
                object = new JSONObject(responseString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {

            Toast.makeText(this, R.string.message_no_response, Toast.LENGTH_SHORT).show();
            return;
        }


        if (object == null) {

            Toast.makeText(this, R.string.message_no_response, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (object.getString("success").equals("true")) {

                JSONObject data = object.getJSONObject("data");

                if (isGettingCompanyListOrSubitAction) {

                    JSONArray listObject = data.getJSONArray("companyList");
                    companies = new Gson().fromJson(listObject.toString(), new TypeToken<List<Company>>() {
                    }.getType());

                    updateCompanySpinner();

                }
                else { //case of submit action

                    Toast.makeText(this, data.getString("message"), Toast.LENGTH_SHORT).show();
                    Globals.coverage.setId(data.getLong("coverage_id"));
                    backToPreviousActivity();
                }

            } else if (object.getString("success").equals("false")) {

                JSONObject data = object.getJSONObject("data");
                Toast.makeText(this, data.getString("message"), Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, R.string.message_no_response, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {

            Toast.makeText(this, R.string.message_no_response, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {

        hideWaitingScreen();
        Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
    }

    private void saveCoverageToDb() {

        isGettingCompanyListOrSubitAction = false;

        JSONObject paramObject = new JSONObject();

        try {

            paramObject.put("access_token", SharedHelper.getKey(this, "access_token"));
            paramObject.put("name", selectedCompany.getName());
            paramObject.put("latitude", Globals.coverage.getLocation().getLatitude());
            paramObject.put("longitude", Globals.coverage.getLocation().getLongitude());
            paramObject.put("address", Globals.coverage.getLocationAddress());
            paramObject.put("company_id", selectedCompany.getId());
            paramObject.put("start_at", Globals.coverage.getDateFrom().getTimeInMillis() / 1000);
            paramObject.put("end_at", Globals.coverage.getDateTo().getTimeInMillis() / 1000);
            paramObject.put("state", CoverageState.UNCOVERED.getIntValue());

        } catch (JSONException e) {

            e.printStackTrace();
            Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
        }

        JsonParser jsonParser = new JsonParser();
        JsonObject gSonObject = (JsonObject) jsonParser.parse(paramObject.toString());

        //get apiInterface
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        //display waiting dialog
        showWaitingScreen();
        //send request

        apiInterface.addCoverage(gSonObject).enqueue(this);

    }

    private void navigateToRecordVehicleActivity() {

        Intent intent = new Intent(StartCoverageActivity.this, RecordVehicleActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.button_back:
                setResult(RESULT_CANCELED);
                finish();
                break;

            case R.id.button_done:

                saveCoverageToDb();


                break;

            case R.id.layout_pickup_location:
                getCurrentAddress();
                break;

            case R.id.layout_start_date:
                showDatePicker(R.id.layout_start_date);
                break;

            case R.id.layout_dropoff_date:
                showDatePicker(R.id.layout_dropoff_date);
                break;

        }
    }

    private void showDatePicker(int resourceId) {

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog pickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                if (resourceId == R.id.layout_start_date) {

                    Globals.coverage.setDateFrom(new GregorianCalendar(year, month, dayOfMonth));
                    textViewStartDate.setText(Globals.coverage.getDateFromString());
                } else if (resourceId == R.id.layout_dropoff_date) {

                    Globals.coverage.setDateTo(new GregorianCalendar(year, month, dayOfMonth));
                    textViewEndDate.setText(Globals.coverage.getDateToString());
                }
            }
        }, year, month, day);

        pickerDialog.show();
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



//        if (!Geocoder.isPresent()) {
//            Toast.makeText(this,
//                    "Can't find current address, ",
//                    Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        Intent intent = new Intent(this, GetAddressIntentService.class);
//        intent.putExtra("add_receiver", addressResultReceiver);
//        intent.putExtra("add_location", currentLocation);
//        startService(intent);
    }

    private String getAddressFromLocation() {

        Geocoder geocoder;
        List<Address> addresses = new ArrayList<>();
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(Globals.coverage.getLocation().getLatitude(), Globals.coverage.getLocation().getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

        return address;
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

    private void updateCompanySpinner() {

        CustomAdapterCompanySelect adapter = new CustomAdapterCompanySelect(getApplicationContext(), companies);
        spinnerCompanySelector.setAdapter(adapter);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        selectedCompany = companies.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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

            //Globals.coverage.setLocationAddress(currentAdd);

            //showResults(currentAdd);
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

    @Override
    public void onBackPressed() {

        backToPreviousActivity();
    }

    private void backToPreviousActivity() {

        setResult(RESULT_OK);
        super.onBackPressed();
    }

}
