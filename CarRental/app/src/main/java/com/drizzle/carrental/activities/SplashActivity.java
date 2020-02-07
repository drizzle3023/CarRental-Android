package com.drizzle.carrental.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.drizzle.carrental.api.ApiClient;
import com.drizzle.carrental.api.ApiInterface;
import com.drizzle.carrental.enumerators.ServiceArea;
import com.drizzle.carrental.globals.Constants;
import com.drizzle.carrental.globals.SharedHelper;
import com.drizzle.carrental.globals.Utils;
import com.drizzle.carrental.models.MyProfile;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.R;
import com.drizzle.carrental.models.VehicleType;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.Iterator;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends Activity implements Callback<ResponseBody> {

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);


        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) &&
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.INTERNET, Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);

        } else {

            runMainProcess();
        }

    }

    private void runMainProcess() {

//        Constants.isRecordingVehicleOrMileOrDamagedPart = 1;
//        Intent intent = new Intent(SplashActivity.this, MyCameraActivity.class);
//        startActivityForResult(intent, 1);

//        return;
//        load saved api token
        SharedHelper.putKey(this, "access_token", "ss7lpzzb3s3yy6hv89bbv7bjjgql199l74effy1o0eerze300i5p5aa9f5hyytnl8kuu9e6vhhn3xvv9mc1lluffwtt03nukk2d26aaejxy11ch00552c92xxt8qimmy");
//
        String strAccessToken = SharedHelper.getKey(this, "access_token");


        if (!strAccessToken.isEmpty()) {

            fetchProfileFromServer();

        } else {

            Globals.isLoggedIn = false;

            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    navigateToOnboardingActivity();
                }
            }, 3000);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    runMainProcess();

                } else {

                    Toast.makeText(this, "Not Granted Permissions.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

    /**
     * fetch user profile from saved access_token
     */
    private void fetchProfileFromServer() {

        //prepare restrofit2 request parameters
        JsonObject gSonObject = new JsonObject();

        //set parameters using org.JSONObject
        JSONObject paramObject = new JSONObject();
        try {

            paramObject.put("access_token", SharedHelper.getKey(this, "access_token"));
        } catch (Exception e) {

            e.printStackTrace();
        }

        JsonParser jsonParser = new JsonParser();
        gSonObject = (JsonObject) jsonParser.parse(paramObject.toString());

        //get apiInterface
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        //send request
        apiInterface.getUserProfile(gSonObject).enqueue(this);
    }

    //callback of success api request
    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

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
            navigateToOnboardingActivity();
            return;
        }


        if (object == null) {
            navigateToOnboardingActivity();
            Toast.makeText(this, R.string.message_no_response, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (object.getString("success").equals("true")) {


                JSONObject data = object.getJSONObject("data");
                JSONObject profileData = data.getJSONObject("profile");

                MyProfile myProfile = new Gson().fromJson(profileData.toString(), new TypeToken<MyProfile>() {
                }.getType());

                Globals.profile = myProfile;

                JSONObject vehicleTypeJSON = null;
                try {
                    vehicleTypeJSON = profileData.getJSONObject("car_type");
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



                    Globals.profile.setVehicleType(vehicleType);
                    Globals.selectedVehicleType = vehicleType;

                    ServiceArea serviceArea = new ServiceArea();

                    if (myProfile.getWorldZone().equals(getString(R.string.worldzone_us_ab))) {
                        serviceArea.setId(1);
                        serviceArea.setAreaName(getString(R.string.worldzone_us));
                    }
                    else if (myProfile.getWorldZone().equals(getString(R.string.worldzone_europe_ab))) {
                        serviceArea.setId(2);
                        serviceArea.setAreaName(getString(R.string.worldzone_europe));
                    }
                    Globals.selectedServiceArea = serviceArea;


                }
                if (data.getString("token_state").equals("valid")) {

                    Iterator<String> keys = object.getJSONObject("data").keys();

                    for (Iterator i = keys; i.hasNext(); ) {

                        if (i.next().equals("refresh_token")) {
                            String newPayload = data.get("refresh_token").toString();
                            String newToken = data.getString("access_token");

                            SharedHelper.putKey(SplashActivity.this, "access_token", newToken);
                            SharedHelper.putKey(SplashActivity.this, "payload", newPayload);

                            //Utils.setAuthHabitSDK(SplashActivity.this);
                        }
                    }
                }

                Globals.isLoggedIn = true;

                Utils.setAuthHabitSDK(SplashActivity.this);

                if (Globals.profile.getPayState() == 1 ) {
                    navigateToHomeActivity();
                }
                else if (Globals.profile.getPayState() == 0){
                    navigateToOnboardingActivity();
                }

            } else if (object.getString("success").equals("false")) {

                JSONObject data = object.getJSONObject("data");
                Toast.makeText(this, data.getString("message"), Toast.LENGTH_SHORT).show();
                navigateToOnboardingActivity();
            } else {

                navigateToOnboardingActivity();
            }
        } catch (Exception e) {

            Toast.makeText(this, R.string.message_no_response, Toast.LENGTH_SHORT).show();
            navigateToOnboardingActivity();
            e.printStackTrace();
        }
    }

    //callback of failed api request
    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {

        navigateToOnboardingActivity();
    }

    private void navigateToHomeActivity() {

        Globals.isLoggedIn = true;

        Intent newIntent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(newIntent);
        finish();
    }

    private void navigateToOnboardingActivity() {

        Intent newIntent = new Intent(SplashActivity.this, OnboardingActivity.class);
        startActivity(newIntent);
        finish();
    }

    private void navigateToPaymentActivity() {

        Intent newIntent = new Intent(SplashActivity.this, PaymentActivity.class);
        startActivity(newIntent);
        finish();
    }

}