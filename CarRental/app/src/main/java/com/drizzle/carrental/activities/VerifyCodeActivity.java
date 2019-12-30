package com.drizzle.carrental.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.drizzle.carrental.api.ApiClient;
import com.drizzle.carrental.api.ApiInterface;
import com.drizzle.carrental.globals.Constants;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.R;
import com.drizzle.carrental.globals.SharedHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class VerifyCodeActivity extends Activity {

    private Button verifyButton;
    private OtpView otpView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifycode);

        verifyButton = (Button) findViewById(R.id.button_verify);
        otpView = findViewById(R.id.otp_view);


        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Send sign up request to server
                final ProgressDialog progressDialog = new ProgressDialog(VerifyCodeActivity.this);
                progressDialog.setMessage("Please wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                JsonObject gsonObject = new JsonObject();
                try {
                    JSONObject paramObject = new JSONObject();

                    paramObject.put("code", otpView.getText().toString());

                    JsonParser jsonParser = new JsonParser();
                    gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

                    Call<ResponseBody> callSync = apiInterface.signIn(gsonObject);
                    Response<ResponseBody> response = callSync.execute();

                    progressDialog.dismiss();

                    JSONObject object = new JSONObject(response.body().string());

                    if (object.getString("success").equalsIgnoreCase("true")){

                        JSONObject data = object.getJSONObject("data");
                        String access_token = data.getString("access_token");

                        SharedHelper.putKey(VerifyCodeActivity.this, "access_token", access_token);

                        Globals.isLoggedIn = true;
                        Intent intent = new Intent(VerifyCodeActivity.this, PaymentActivity.class);
                        finish();
                        startActivity(intent);

                    } else {

                        JSONObject data = object.getJSONObject("data");
                        Toast.makeText(VerifyCodeActivity.this, data.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){

                    if (progressDialog.isShowing())
                        progressDialog.dismiss();

                    e.printStackTrace();
                    Toast.makeText(VerifyCodeActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }

            }
        });

        otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {

                enableVerifyButton(true);

            }
        });

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

}
