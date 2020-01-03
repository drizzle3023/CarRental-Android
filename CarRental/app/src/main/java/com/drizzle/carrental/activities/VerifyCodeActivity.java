package com.drizzle.carrental.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.drizzle.carrental.api.ApiClient;
import com.drizzle.carrental.api.ApiInterface;
import com.drizzle.carrental.R;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.globals.SharedHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.drizzle.carrental.globals.Globals.stringPhoneNumber;

public class VerifyCodeActivity extends Activity implements View.OnClickListener, OnOtpCompletionListener, Callback<ResponseBody> {

    private Button verifyButton;
    private OtpView otpView;

    private String stringOtp;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifycode);

        progressDialog = new ProgressDialog(this);

        verifyButton = findViewById(R.id.button_verify);
        otpView = findViewById(R.id.otp_view);


        verifyButton.setOnClickListener(this);

        otpView.setOtpCompletionListener(this);

        otpView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (otpView.getText().length() == 4) {
                    enableVerifyButton(true);
                }
                else {
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
        }
        else {
            verifyButton.setBackgroundResource(R.drawable.inactive_button);
        }
    }

    private void submitVerifyRequestToServer() {


        // Send sign up request to server
        JSONObject paramObject = new JSONObject();

        try {

            paramObject.put("mobile", Globals.stringPhoneNumber);
            paramObject.put("code", stringOtp);

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

                if (Globals.isSignUpOrLoginRequest) {
                    navigateToPaymentActivity();
                }
                else {
                    Globals.isLoggedIn = true;
                    JSONObject dataObject = object.getJSONObject("data");
                    SharedHelper.putKey(this, "access_token", dataObject.getString("access_token"));
                    navigateToHomeActivity();
                }
            } else if (object.getString("success").equals("false")) {

                JSONObject data = object.getJSONObject("data");
                Toast.makeText(this, data.getString("message"), Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {

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
    }

    @Override
    public void onOtpCompleted(String otp) {

        stringOtp = otp;
        enableVerifyButton(true);
    }

    private void showWaitingScreen() {

        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideWaitingScreen() {

        progressDialog.dismiss();
    }

    private void navigateToPaymentActivity() {

        Intent newIntent = new Intent(this, PaymentActivity.class);
        startActivity(newIntent);
    }

    private void navigateToHomeActivity() {

        Intent newIntent = new Intent(this, HomeActivity.class);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(newIntent);
        finish();
    }
}
