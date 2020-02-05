package com.drizzle.carrental.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.drizzle.carrental.api.ApiClient;
import com.drizzle.carrental.api.ApiInterface;
import com.drizzle.carrental.R;
import com.drizzle.carrental.enumerators.ServiceArea;
import com.drizzle.carrental.globals.Constants;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.globals.SharedHelper;
import com.drizzle.carrental.globals.Utils;
import com.drizzle.carrental.models.MyProfile;
import com.drizzle.carrental.models.VehicleType;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyCodeActivity extends Activity implements View.OnClickListener, OnOtpCompletionListener, Callback<ResponseBody> {

    private Button verifyButton;
    private OtpView otpView;
    private ImageButton buttonBack;
    private String stringOtp;

    private TextView textViewContentView;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifycode);

        progressDialog = new ProgressDialog(this);

        buttonBack = findViewById(R.id.button_back_to_onboarding);

        verifyButton = findViewById(R.id.button_verify);
        otpView = findViewById(R.id.otp_view);
        textViewContentView = findViewById(R.id.textview_contentview);

        textViewContentView.setText(getResources().getString(R.string.enter_verification_code));

        verifyButton.setOnClickListener(this);

        buttonBack.setOnClickListener(this);

        otpView.setOtpCompletionListener(this);

        otpView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (otpView.getText().length() == 4) {
                    enableVerifyButton(true);
                } else {
                    enableVerifyButton(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    public void enableVerifyButton(boolean isEnable) {

        verifyButton.setEnabled(isEnable);

        if (isEnable) {
            verifyButton.setBackgroundResource(R.drawable.active_button);
            otpView.setTextColor(getResources().getColor(R.color.colorNormalBlue, null));
            otpView.setLineColor(getResources().getColor(R.color.colorNormalBlue, null));
        } else {
            verifyButton.setBackgroundResource(R.drawable.inactive_button);
            otpView.setTextColor(getResources().getColor(R.color.colorNormalBlue, null));
            otpView.setLineColor(getResources().getColor(R.color.colorOtpLineInvalid, null));
        }
    }

    private void submitVerifyRequestToServer() {


        // Send sign up request to server
        JSONObject paramObject = new JSONObject();

        try {

            paramObject.put("mobile", Globals.mobileNumber);
            paramObject.put("code", stringOtp);

        } catch (Exception e) {

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
        apiInterface.signVerify(gSonObject).enqueue(this);
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

                MyProfile myProfile = new MyProfile();

                JSONObject profileObject = null;
                try {
                    profileObject = object.getJSONObject("data").getJSONObject("user_profile");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (profileObject != null) {

                    try {
                        myProfile.setName(profileObject.getString("name"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        myProfile.setEmail(profileObject.getString("email"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        myProfile.setMobile(profileObject.getString("mobile"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        myProfile.setAddress(profileObject.getString("address"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        myProfile.setWorldZone(profileObject.getString("world_zone"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        myProfile.setPayState(profileObject.getInt("pay_state"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    JSONObject vehicleTypeJSON = null;
                    try {
                        vehicleTypeJSON = profileObject.getJSONObject("car_type");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (vehicleTypeJSON != null) {

                        VehicleType vehicleType = new VehicleType();
                        try {
                            vehicleType.setId(vehicleTypeJSON.getLong("id"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            vehicleType.setName(vehicleTypeJSON.getString("name"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        try {
                            vehicleType.setPricePerYearUsd(vehicleTypeJSON.getDouble("price_per_year_usd"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            vehicleType.setPricePerYearEur(vehicleTypeJSON.getDouble("price_per_year_eur"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        try {
                            vehicleType.setIconURL(Constants.MEDIA_PATH_URL + vehicleTypeJSON.getString("icon_url"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        myProfile.setVehicleType(vehicleType);

                        Globals.profile.setVehicleType(vehicleType);
                        Globals.selectedVehicleType = vehicleType;

                        ServiceArea serviceArea = new ServiceArea();

                        if (myProfile.getWorldZone().equals(getString(R.string.worldzone_us_ab))) {
                            serviceArea.setId(1);
                            serviceArea.setAreaName(getString(R.string.worldzone_us));
                        } else if (myProfile.getWorldZone().equals(getString(R.string.worldzone_europe_ab))) {
                            serviceArea.setId(2);
                            serviceArea.setAreaName(getString(R.string.worldzone_europe));
                        }
                        Globals.selectedServiceArea = serviceArea;
                    }
                }


                if (Globals.isSignUpOrLoginRequest) {

                    Globals.profile = myProfile;

                    JSONObject dataObject = object.getJSONObject("data");
                    SharedHelper.putKey(this, "access_token", dataObject.getString("access_token"));
                    SharedHelper.putKey(this, "payload", dataObject.getJSONObject("user").toString());

                    Globals.isLoggedIn = true;

                    if (Globals.profile.getPayState() == 1) {

                    } else if (Globals.profile.getPayState() == 0) {

                        if (Globals.selectedServiceArea == null || Globals.selectedVehicleType == null) {
                            navigateToHomeActivity();
                        }
                        else {
                            navigateToPaymentActivity();
                        }

                    }


                } else {

                    Globals.profile = myProfile;

                    Globals.isLoggedIn = true;
                    JSONObject dataObject = object.getJSONObject("data");
                    SharedHelper.putKey(this, "access_token", dataObject.getString("access_token"));
                    SharedHelper.putKey(this, "payload", dataObject.getJSONObject("user").toString());


                    navigateToHomeActivity();

                }

                Utils.setAuthHabitSDK(this);


            } else if (object.getString("success").equals("false")) {

                enableVerifyButton(false);
                textViewContentView.setText(getResources().getString(R.string.incorrect_verficiation_code));
                textViewContentView.setTextColor(Color.RED);
                otpView.setTextColor(Color.RED);

                JSONObject data = object.getJSONObject("data");
                Toast.makeText(this, data.getString("message"), Toast.LENGTH_SHORT).show();


            } else {

                Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

            Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {

        hideWaitingScreen();
        Toast.makeText(this, R.string.message_no_response, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.button_verify) {
            submitVerifyRequestToServer();
        }
        if (v.getId() == R.id.button_back_to_onboarding) {
            finish();
        }
    }

    @Override
    public void onOtpCompleted(String otp) {

        stringOtp = otp;
        enableVerifyButton(true);
    }

    private void showWaitingScreen() {

        try {
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideWaitingScreen() {

        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateToPaymentActivity() {

        Intent newIntent = new Intent(this, PaymentActivity.class);
        startActivity(newIntent);
        finish();
    }

    private void navigateToHomeActivity() {

        Intent newIntent = new Intent(this, HomeActivity.class);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(newIntent);
        finish();
    }

}
