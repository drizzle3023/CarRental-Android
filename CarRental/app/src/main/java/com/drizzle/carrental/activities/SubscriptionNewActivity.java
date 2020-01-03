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
import com.drizzle.carrental.globals.SharedHelper;
import com.drizzle.carrental.models.MyProfile;
import com.drizzle.carrental.models.SubscriptionModel;
import com.drizzle.carrental.models.VehicleType;
import com.drizzle.carrental.serializers.ParseHistory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

        buttonBack = (ImageButton) findViewById(R.id.button_back);
        buttonBack.setOnClickListener(this);

        buttonSubscribe = (Button) findViewById(R.id.button_subscribe);
        buttonSubscribe.setOnClickListener(this);

        checkBoxUs = (RadioButton) findViewById(R.id.checkbox_us);
        checkBoxEurope = (RadioButton) findViewById(R.id.checkbox_europe);

        checkBoxUs.setOnCheckedChangeListener(this);
        checkBoxEurope.setOnCheckedChangeListener(this);

        textViewPrice = (TextView) findViewById(R.id.textview_price);


    }

    private void updateView() {

        CustomAdapterSubscriptionCarTypeSelect customAdapter = new CustomAdapterSubscriptionCarTypeSelect(getApplicationContext(), vehicleTypes);
        spinner.setAdapter(customAdapter);

        DecimalFormat df = new DecimalFormat("0.00");

        textViewPrice.setText(df.format(Globals.selectedVehicleType.getPricePerYear()) + "€ / per year");

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
        item1.setAreaName("United States");
        ServiceArea item2 = new ServiceArea();
        item2.setId(2);
        item2.setAreaName("Europe");

        serviceAreas.add(item1);
        serviceAreas.add(item2);


        /**
         * Init local variables
         */


        if (!serviceAreas.isEmpty()) {
            Globals.selectedServiceArea = serviceAreas.get(0);
        }
    }

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        //Toast.makeText(getApplicationContext(), countryNames[position], Toast.LENGTH_LONG).show();

        Globals.selectedVehicleType = vehicleTypes.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button_subscribe) {
            Constants.isNavigateToSignupOrLogin = true;
            Intent intent = new Intent(SubscriptionNewActivity.this, SignUpLoginActivity.class);
            startActivity(intent);
        }

        if (view.getId() == R.id.button_back) {
            finish();
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton.getId() == R.id.checkbox_us) {

            Globals.selectedServiceArea = serviceAreas.get(1);

        } else if (compoundButton.getId() == R.id.checkbox_europe) {

            Globals.selectedServiceArea= serviceAreas.get(0);

        }
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
                JSONArray listObject = data.getJSONArray("carTypeList");
                vehicleTypes = new Gson().fromJson(listObject.toString(), new TypeToken<List<VehicleType>>() {}.getType());

                if (!vehicleTypes.isEmpty()) {
                    try {
                        Globals.selectedVehicleType = vehicleTypes.get(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                updateView();
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
    }
}