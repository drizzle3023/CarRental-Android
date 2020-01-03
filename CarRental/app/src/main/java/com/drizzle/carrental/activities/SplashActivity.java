package com.drizzle.carrental.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.drizzle.carrental.api.ApiClient;
import com.drizzle.carrental.api.ApiInterface;
import com.drizzle.carrental.globals.Constants;
import com.drizzle.carrental.globals.SharedHelper;
import com.drizzle.carrental.models.MyProfile;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends Activity implements Callback<ResponseBody> {

    ProgressDialog progressDialog;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        progressDialog = new ProgressDialog(this);

        //load saved api token
//        SharedHelper.putKey(this, "access_token", "bstohcty6u56epm09pnplrlcgpv07dj6ur6korqomx2nk0lmcy8w97anye3pxj7xoey46ckmabnp7pht3t92ssgaoy5t007ojy557aaoimc2yw25tg2ke314bdw5w6m4");
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
        apiInterface.getUserProfile(gSonObject).enqueue(this);
    }

    private void showWaitingScreen() {

        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideWaitingScreen() {

        progressDialog.dismiss();
    }

    //callback of success api request
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
            navigateToOnboardingActivity();
            return;
        }


        if (object == null) {

            Toast.makeText(this, R.string.message_no_response, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (object.getString("success").equals("true")) {


                JSONObject data = object.getJSONObject("data");
                JSONObject profileData = data.getJSONObject("profile");
                MyProfile myProfile = new Gson().fromJson(profileData.toString(), new TypeToken<MyProfile>() {}.getType());

                Globals.profile = myProfile;

                navigateToHomeActivity();

            } else if (object.getString("success").equals("false")) {

                JSONObject data = object.getJSONObject("data");
                Toast.makeText(this, data.getString("message"), Toast.LENGTH_SHORT).show();
                navigateToOnboardingActivity();
            } else {

                navigateToOnboardingActivity();
            }
        } catch (JSONException e) {

            Toast.makeText(this, R.string.message_no_response, Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }
        navigateToOnboardingActivity();
    }

    //callback of failed api request
    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {

        hideWaitingScreen();
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

}