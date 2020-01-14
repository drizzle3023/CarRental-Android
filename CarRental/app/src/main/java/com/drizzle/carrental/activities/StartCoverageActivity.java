package com.drizzle.carrental.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.drizzle.carrental.R;
import com.drizzle.carrental.adapters.CustomAdapterCompanySelect;
import com.drizzle.carrental.api.ApiClient;
import com.drizzle.carrental.api.ApiInterface;
import com.drizzle.carrental.api.VolleyMultipartRequest;
import com.drizzle.carrental.enumerators.CoverageState;
import com.drizzle.carrental.globals.Constants;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.globals.SharedHelper;
import com.drizzle.carrental.globals.Utils;
import com.drizzle.carrental.models.Company;
import com.drizzle.carrental.models.Coverage;
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


import net.gotev.uploadservice.data.UploadInfo;
import net.gotev.uploadservice.data.UploadNotificationConfig;
import net.gotev.uploadservice.network.ServerResponse;
import net.gotev.uploadservice.observer.request.RequestObserverDelegate;
import net.gotev.uploadservice.observer.task.UploadTaskObserver;
import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

    private boolean isGettingCompanyListOrSubmitAction = true;

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

                isGettingCompanyListOrSubmitAction = true;

                fetchCompaniesFromServer();

                Globals.coverage.setLocation(currentLocation);
                String strAddress = Utils.getAddressFromLocation(getApplicationContext(), Globals.coverage.getLocation());
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

        progressDialog = new ProgressDialog(this);
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

                if (isGettingCompanyListOrSubmitAction) {

                    JSONArray listObject = data.getJSONArray("companyList");
                    companies = new Gson().fromJson(listObject.toString(), new TypeToken<List<Company>>() {
                    }.getType());

                    if (!companies.isEmpty()) {
                        updateCompanySpinner();
                        selectedCompany = companies.get(0);
                        Globals.coverage.setCompany(selectedCompany);
                    }
                } else { //case of submit action

                    Toast.makeText(this, data.getString("message"), Toast.LENGTH_SHORT).show();
                    Globals.coverage.setId(data.getLong("coverage_id"));
                    backToPreviousActivity();
                }

            } else if (object.getString("success").equals("false")) {

                JSONObject data = object.getJSONObject("data");
                if (isGettingCompanyListOrSubmitAction) {
                    Globals.coverage = new Coverage();
                }
                Toast.makeText(this, data.getString("message"), Toast.LENGTH_SHORT).show();
            } else {
                if (isGettingCompanyListOrSubmitAction) {
                    Globals.coverage = new Coverage();
                }
                Toast.makeText(this, R.string.message_no_response, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {

            if (isGettingCompanyListOrSubmitAction) {
                Globals.coverage = new Coverage();
            }
            Toast.makeText(this, R.string.message_no_response, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {

        hideWaitingScreen();
        Globals.coverage = new Coverage();
        Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
    }

    private void saveCoverageToDb() {

        if (Globals.coverage.getCompany() == null) {

            Toast.makeText(this, getResources().getString(R.string.message_company_id_is_missing), Toast.LENGTH_SHORT).show();
            return;
        }
        if (Globals.coverage.getCompany().getName().isEmpty() || !(Globals.coverage.getCompany().getId() > 0)) {

            Toast.makeText(this, getResources().getString(R.string.message_company_id_is_missing), Toast.LENGTH_SHORT).show();
            return;
        }
        if (Globals.coverage.getLocationAddress().isEmpty()) {

            Toast.makeText(this, getResources().getString(R.string.message_address_is_missing), Toast.LENGTH_SHORT).show();
            return;
        }
        if (Globals.coverage.getDateFrom() == null) {

            Toast.makeText(this, getResources().getString(R.string.message_startdate_is_missing), Toast.LENGTH_SHORT).show();
            return;
        }
        if (Globals.coverage.getDateTo() == null) {

            Toast.makeText(this, getResources().getString(R.string.message_enddate_is_missing), Toast.LENGTH_SHORT).show();
            return;
        }
        if (Globals.coverage.getDateTo().before(Globals.coverage.getDateFrom())) {

            Toast.makeText(this, getResources().getString(R.string.message_period_is_not_correct), Toast.LENGTH_SHORT).show();
            return;
        }

        isGettingCompanyListOrSubmitAction = false;
//
//        JSONObject paramObject = new JSONObject();
//
//        try {
//
//            paramObject.put("access_token", SharedHelper.getKey(this, "access_token"));
//            paramObject.put("name", selectedCompany.getName());
//            paramObject.put("latitude", Globals.coverage.getLocation().getLatitude());
//            paramObject.put("longitude", Globals.coverage.getLocation().getLongitude());
//            paramObject.put("address", Globals.coverage.getLocationAddress());
//            paramObject.put("company_id", selectedCompany.getId());
//            paramObject.put("start_at", Globals.coverage.getDateFrom().getTimeInMillis() / 1000);
//            paramObject.put("end_at", Globals.coverage.getDateTo().getTimeInMillis() / 1000);
//            paramObject.put("state", CoverageState.UNCOVERED.getIntValue());
//
//        } catch (JSONException e) {
//
//            e.printStackTrace();
//            Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
//        }
//
//        JsonParser jsonParser = new JsonParser();
//        JsonObject gSonObject = (JsonObject) jsonParser.parse(paramObject.toString());
//
//        //get apiInterface
//        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//        //display waiting dialog
//        showWaitingScreen();
//        //send request
//
//        apiInterface.addCoverage(gSonObject).enqueue(this);

        /*
        MultiPartUploadRequest
         */
//
//        MultipartUploadRequest multipartUploadRequest = null;
//
//        multipartUploadRequest = new MultipartUploadRequest(this, Constants.SERVER_HTTP_URL + "/api/" + "add-coverage");
//        multipartUploadRequest.setMethod("POST");
//
//
//        //multipartUploadRequest.addFileToUpload(BaseCameraActivity.getVideoFilePath(), "video-vehicle");
//
//        multipartUploadRequest.addParameter("access_token", SharedHelper.getKey(this, "access_token"));
//        multipartUploadRequest.addParameter("name", selectedCompany.getName());
//        multipartUploadRequest.addParameter("latitude", Double.valueOf(Globals.coverage.getLocation().getLatitude()).toString());
//        multipartUploadRequest.addParameter("longitude", Double.valueOf(Globals.coverage.getLocation().getLongitude()).toString());
//        multipartUploadRequest.addParameter("address", Globals.coverage.getLocationAddress());
//        multipartUploadRequest.addParameter("company_id", Long.valueOf(selectedCompany.getId()).toString());
//        multipartUploadRequest.addParameter("start_at", Long.valueOf(Globals.coverage.getDateFrom().getTimeInMillis() / 1000).toString());
//        multipartUploadRequest.addParameter("end_at", Long.valueOf(Globals.coverage.getDateTo().getTimeInMillis() / 1000).toString());
//        multipartUploadRequest.addParameter("state", Integer.valueOf(CoverageState.UNCOVERED.getIntValue()).toString());
//
//
//        multipartUploadRequest.startUpload();
//
//        multipartUploadRequest.subscribe(this, new RequestObserverDelegate() {
//
//
//            @Override
//            public void onCompleted(@NotNull Context context, @NotNull UploadInfo uploadInfo) {
//
//            }
//
//            @Override
//            public void onCompletedWhileNotObserving() {
//
//            }
//
//            @Override
//            public void onError(@NotNull Context context, @NotNull UploadInfo uploadInfo, @NotNull Throwable throwable) {
//                Globals.coverage = new Coverage();
//                Toast.makeText(StartCoverageActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onProgress(@NotNull Context context, @NotNull UploadInfo uploadInfo) {
//
//            }
//
//            @Override
//            public void onSuccess(@NotNull Context context, @NotNull UploadInfo uploadInfo, @NotNull ServerResponse serverResponse) {
//
//                String responseString = serverResponse.getBodyString();
//
//                JSONObject object = null;
//                if (responseString != null) {
//                    try {
//                        object = new JSONObject(responseString);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//
//                    Toast.makeText(StartCoverageActivity.this, R.string.message_no_response, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if (object == null) {
//
//                    Toast.makeText(StartCoverageActivity.this, R.string.message_no_response, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                try {
//                    if (object.getString("success").equals("true")) {
//
//                        JSONObject data = object.getJSONObject("data");
//                        Toast.makeText(StartCoverageActivity.this, data.getString("message"), Toast.LENGTH_SHORT).show();
//                        Globals.coverage.setId(data.getLong("coverage_id"));
//                        backToPreviousActivity();
//
//                    } else if (object.getString("success").equals("false")) {
//                        JSONObject data = object.getJSONObject("data");
//                        Toast.makeText(StartCoverageActivity.this, data.getString("message"), Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(StartCoverageActivity.this, R.string.message_no_response, Toast.LENGTH_SHORT).show();
//                    }
//                } catch (JSONException e) {
//
//                    Toast.makeText(StartCoverageActivity.this, R.string.message_no_response, Toast.LENGTH_SHORT).show();
//                    e.printStackTrace();
//                }
//            }
//        });

        showWaitingScreen();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(
                Request.Method.POST, Constants.SERVER_HTTP_URL + "/api/add-coverage",
                new com.android.volley.Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideWaitingScreen();

                        String res = new String(response.data);
                        try {
                            JSONObject jsonObject = new JSONObject(res);
                            JSONObject data = jsonObject.getJSONObject("data");

                            if (jsonObject.getString("success").equals("true")) {

                                Toast.makeText(StartCoverageActivity.this, data.getString("message"), Toast.LENGTH_SHORT).show();
                                backToPreviousActivity();
                            } else {
                                Toast.makeText(StartCoverageActivity.this, data.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(StartCoverageActivity.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideWaitingScreen();
                Toast.makeText(StartCoverageActivity.this, getResources().getString(R.string.message_no_response), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> paramObject = new HashMap<>();

                paramObject.put("access_token", SharedHelper.getKey(StartCoverageActivity.this, "access_token"));
                paramObject.put("name", selectedCompany.getName());
                paramObject.put("latitude", String.format("%f", Globals.coverage.getLocation().getLatitude()));
                paramObject.put("longitude", String.format("%f", Globals.coverage.getLocation().getLongitude()));
                paramObject.put("address", Globals.coverage.getLocationAddress());
                paramObject.put("company_id", String.format("%d", selectedCompany.getId()));
                paramObject.put("start_at", String.format("%d", Globals.coverage.getDateFrom().getTimeInMillis() / 1000));
                paramObject.put("end_at", String.format("%d", Globals.coverage.getDateTo().getTimeInMillis() / 1000));
                paramObject.put("state", String.format("%d", CoverageState.UNCOVERED.getIntValue()));

                return paramObject;
            }

            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() throws AuthFailureError {
                Map<String, VolleyMultipartRequest.DataPart> params = new HashMap<>();


                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(volleyMultipartRequest);
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

                    GregorianCalendar currentDate = new GregorianCalendar();
                    currentDate.set(Calendar.HOUR, 0);
                    currentDate.set(Calendar.MINUTE, 0);
                    currentDate.set(Calendar.SECOND, 0);
                    currentDate.set(Calendar.MILLISECOND, 0);
                    currentDate.set(Calendar.AM_PM, Calendar.AM);

                    GregorianCalendar selectedDate = new GregorianCalendar(year, month, dayOfMonth);

                    if (selectedDate.before(currentDate)) {

                        Globals.coverage.setDateFrom(null);
                        textViewStartDate.setText("");
                        Toast.makeText(getBaseContext(), R.string.pickup_date_is_past, Toast.LENGTH_SHORT).show();

                    } else {

                        Globals.coverage.setDateFrom(new GregorianCalendar(year, month, dayOfMonth));
                        textViewStartDate.setText(Globals.coverage.getDateFromString());
                    }

                } else if (resourceId == R.id.layout_dropoff_date) {

                    if (Globals.coverage.getDateFrom() == null) {

                        Toast.makeText(getBaseContext(), R.string.pickup_date_is_null, Toast.LENGTH_SHORT).show();
                        textViewEndDate.setText("");
                        return;
                    } else {

                        GregorianCalendar pickupDate = Globals.coverage.getDateFrom();
                        GregorianCalendar selectedDate = new GregorianCalendar(year, month, dayOfMonth);

                        if (selectedDate.before(pickupDate)) {

                            Globals.coverage.setDateTo(null);
                            textViewEndDate.setText("");
                            Toast.makeText(getBaseContext(), R.string.dropoff_date_is_past, Toast.LENGTH_SHORT).show();
                        } else {
                            Globals.coverage.setDateTo(new GregorianCalendar(year, month, dayOfMonth));
                            textViewEndDate.setText(Globals.coverage.getDateToString());

                        }
                    }
                }
            }
        }, year, month, day);

        Window window = pickerDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);


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
