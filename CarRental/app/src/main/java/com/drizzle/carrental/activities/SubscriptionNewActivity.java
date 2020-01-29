package com.drizzle.carrental.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.*;
import android.view.View;

import com.drizzle.carrental.adapters.CustomAdapterSubscriptionCarTypeSelect;
import com.drizzle.carrental.R;
import com.drizzle.carrental.api.ApiClient;
import com.drizzle.carrental.api.ApiInterface;
import com.drizzle.carrental.enumerators.ServiceArea;
import com.drizzle.carrental.globals.Constants;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.models.SubscriptionModel;
import com.drizzle.carrental.models.VehicleType;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscriptionNewActivity extends Activity implements AdapterView.OnItemSelectedListener, View.OnClickListener, RadioButton.OnCheckedChangeListener, Callback<ResponseBody> {

    ProgressDialog progressDialog;

    //variables to fetch data from database
    ArrayList<VehicleType> vehicleTypes;
    ArrayList<ServiceArea> serviceAreas;
    SubscriptionModel subscriptionInfo;

    //control handlers
    Handler handler;
    private Spinner spinner;
    private Button buttonSubscribe;
    private ImageButton buttonBack;
    private RadioButton checkBoxUs;
    private RadioButton checkBoxEurope;
    private TextView textViewPrice;

    private boolean isSignedUpOrNot = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_new);

        progressDialog = new ProgressDialog(this);

        getControlHandlersAndLinkActions();


        initVariables();


    }

    private void getControlHandlersAndLinkActions() {

        spinner = (Spinner) findViewById(R.id.subscriptionCarTypeSpinner);
        spinner.setOnItemSelectedListener(this);

        buttonBack = (ImageButton) findViewById(R.id.button_back_to_onboarding);
        buttonBack.setOnClickListener(this);

        buttonSubscribe = (Button) findViewById(R.id.button_subscribe);
        buttonSubscribe.setOnClickListener(this);

        checkBoxUs = (RadioButton) findViewById(R.id.checkbox_us);
        checkBoxEurope = (RadioButton) findViewById(R.id.checkbox_europe);

        checkBoxUs.setOnCheckedChangeListener(this);
        checkBoxEurope.setOnCheckedChangeListener(this);

        checkBoxUs.setOnClickListener(this);
        checkBoxEurope.setOnClickListener(this);

        textViewPrice = (TextView) findViewById(R.id.textview_price);



    }

    private void updateView() {

        DecimalFormat df = new DecimalFormat("0.00");

        if (Globals.selectedServiceArea == null) {

            Toast.makeText(this, getString(R.string.message_service_is_not_defined), Toast.LENGTH_SHORT).show();
            textViewPrice.setText(getString(R.string.message_service_is_not_defined));
            return;
        }
        if (Globals.selectedServiceArea.getAreaName().equals(getString(R.string.worldzone_us))) {

            textViewPrice.setText(df.format(Globals.selectedVehicleType.getPricePerYearUsd()) + getResources().getString(R.string.usd_character) + " / per year");
        }
        else if (Globals.selectedServiceArea.getAreaName().equals(getString(R.string.worldzone_europe))) {

            textViewPrice.setText(df.format(Globals.selectedVehicleType.getPricePerYearEur()) + getResources().getString(R.string.euro_character) + " / per year");
        }
    }

    private void initVariables() {

        vehicleTypes = new ArrayList<>();
        serviceAreas = new ArrayList<>();

        /**
         * Fetch data  from server
         */
        // - fetch vehicle types
        //should be replaced with API

        fetchCarTypeListFromServer();

        // - fetch service area
        //should be replaced with API
        ServiceArea item1 = new ServiceArea();
        item1.setId(1);
        item1.setAreaName(getString(R.string.worldzone_us));
        ServiceArea item2 = new ServiceArea();
        item2.setId(2);
        item2.setAreaName(getString(R.string.worldzone_europe));

        serviceAreas.add(item1);
        serviceAreas.add(item2);

        /**
         * Init local variables
         */

        if (Globals.profile != null) {

            if (Globals.profile.getMobile() != null && !Globals.profile.getMobile().isEmpty()) {

                isSignedUpOrNot = true;
            }
        }

    }

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {

        Globals.selectedVehicleType = vehicleTypes.get(position);

        updateView();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.checkbox_europe || view.getId() == R.id.checkbox_us) {

            if (!serviceAreas.isEmpty()) {
                if (checkBoxUs.isChecked()) {
                    Globals.selectedServiceArea = serviceAreas.get(0);
                }
                else if (checkBoxEurope.isChecked()) {
                    Globals.selectedServiceArea = serviceAreas.get(1);
                }
                else {
                    Globals.selectedServiceArea = null;
                }
            }

            updateView();
        }
        else if (view.getId() == R.id.button_subscribe) {

            if (Globals.selectedVehicleType == null) {
                Toast.makeText(this, getResources().getString(R.string.no_vehicle_types), Toast.LENGTH_SHORT).show();
                return;
            }
            if (Globals.selectedServiceArea == null) {
                Toast.makeText(this, getResources().getString(R.string.no_service_area), Toast.LENGTH_SHORT).show();
                return;
            }

            if (isSignedUpOrNot) {

                Intent intent = new Intent(SubscriptionNewActivity.this, PaymentActivity.class);
                startActivity(intent);
            }
            else {
                Constants.isNavigateToSignupOrLogin = true;
                Intent intent = new Intent(SubscriptionNewActivity.this, SignUpLoginActivity.class);
                startActivity(intent);
            }

        }

        if (view.getId() == R.id.button_back_to_onboarding) {
            finish();
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//
//        if (compoundButton.getId() == R.id.checkbox_us) {
//
//            Globals.selectedServiceArea = serviceAreas.get(1);
//
//        } else if (compoundButton.getId() == R.id.checkbox_europe) {
//
//            Globals.selectedServiceArea= serviceAreas.get(0);
//
//        }
//
//        updateView();
    }

    private void fetchCarTypeListFromServer() {

        //prepare restrofit2 request parameters
        JsonObject gSonObject = new JsonObject();

        //set parameters using org.JSONObject
        JSONObject paramObject = new JSONObject();

        JsonParser jsonParser = new JsonParser();
        gSonObject = (JsonObject) jsonParser.parse(paramObject.toString());

        //get apiInterface
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        //display waiting dialog
        showWaitingScreen();
        //send request
        apiInterface.getCarTypeList(gSonObject).enqueue(this);
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject object = null;
        if (responseString != null) {
            try {
                object = new JSONObject(responseString);
            } catch (Exception e) {
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
                JSONArray listObject = data.getJSONArray("carTypeList");
                vehicleTypes = new Gson().fromJson(listObject.toString(), new TypeToken<List<VehicleType>>() {}.getType());

                if (!vehicleTypes.isEmpty()) {
                    try {
                        Globals.selectedVehicleType = vehicleTypes.get(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                CustomAdapterSubscriptionCarTypeSelect customAdapter = new CustomAdapterSubscriptionCarTypeSelect(getApplicationContext(), vehicleTypes);
                spinner.setAdapter(customAdapter);

                if (Globals.profile != null) {

                    if (Globals.profile.getVehicleType() != null && Globals.profile.getWorldZone() != null && !Globals.profile.getWorldZone().isEmpty()) {

                        for (int i = 0; i < vehicleTypes.size(); i ++) {
                            if (vehicleTypes.get(i).getId() == Globals.profile.getVehicleType().getId()) {

                                spinner.setSelection(i);
                                break;
                            }
                        }

                        if (Globals.profile.getWorldZone().equals(getString(R.string.worldzone_europe))) {

                            checkBoxEurope.setChecked(true);
                            checkBoxUs.setChecked(false);
                        }
                        else if (Globals.profile.getWorldZone().equals(getString(R.string.worldzone_us))) {
                            checkBoxEurope.setChecked(false);
                            checkBoxUs.setChecked(true);
                        }
                    }
                }

                checkBoxUs.performClick();

            } else if (object.getString("success").equals("false")) {

                JSONObject data = object.getJSONObject("data");
                Toast.makeText(this, data.getString("message"), Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, R.string.message_no_response, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

            Toast.makeText(this, R.string.message_no_response, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {

        hideWaitingScreen();
    }
}