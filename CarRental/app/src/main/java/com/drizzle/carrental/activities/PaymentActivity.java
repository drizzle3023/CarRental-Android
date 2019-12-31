package com.drizzle.carrental.activities;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.adyen.checkout.base.model.PaymentMethodsApiResponse;
import com.adyen.checkout.base.model.paymentmethods.PaymentMethod;
import com.adyen.checkout.card.CardComponent;
import com.adyen.checkout.card.CardConfiguration;
import com.adyen.checkout.core.api.Environment;
import com.adyen.checkout.dropin.DropIn;
import com.adyen.checkout.dropin.DropInConfiguration;
import com.drizzle.carrental.R;
import com.drizzle.carrental.api.ApiClient;
import com.drizzle.carrental.api.ApiInterface;
import com.drizzle.carrental.globals.Constants;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.services.YourDropService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends FragmentActivity{

    private Button buttonPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        buttonPay = findViewById(R.id.button_pay);
        buttonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                getPaymentMethods();

//                Intent intent=new Intent(PaymentActivity.this,SubscribeSuccessActivity.class);
//                startActivity(intent);
            }
        });

    }

    /* Get available payment methods from the server.  */
    private void getPaymentMethods() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("access_token", "bstohcty6u56epm09pnplrlcgpv07dj6ur6korqomx2nk0lmcy8w97anye3pxj7xoey46ckmabnp7pht3t92ssgaoy5t007ojy557aaoimc2yw25tg2ke314bdw5w6m4");
            paramObject.put("car_type_id", 1);

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.getPaymentMethods(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    progressDialog.dismiss();

                    try {
                        JSONObject object = new JSONObject(response.body().string());

                        if (object.getString("success").equals("true")){

                            JSONObject data = object.getJSONObject("data");
                            JSONObject paymentMethods = data.getJSONObject("paymentMethods");
                            Globals.paymentId = data.getInt("payment_id");

                            Log.i("Payment_methods", paymentMethods.toString());

                            // Import available payment methods and set them to DropIn.
                            PaymentMethodsApiResponse paymentMethodsApiResponse = PaymentMethodsApiResponse.SERIALIZER.deserialize(paymentMethods);
                            CardConfiguration cardConfiguration = new CardConfiguration.Builder(Locale.getDefault(), Environment.TEST, Constants.ADYEN_PAYMENT_PUBLIC_KEY).build();

                            // After payment succeed, call this intent.
                            Intent resultintent = new Intent(PaymentActivity.this, SubscribeSuccessActivity.class);

                            DropInConfiguration dropInConfiguration = new DropInConfiguration.Builder(
                                    PaymentActivity.this,
                                    resultintent,
                                    YourDropService.class).addCardConfiguration(cardConfiguration).build();

                            DropIn.startPayment(PaymentActivity.this, paymentMethodsApiResponse, dropInConfiguration);

                        } else{
                            JSONObject data = object.getJSONObject("data");
                            Toast.makeText(PaymentActivity.this, data.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(PaymentActivity.this, "Server connect error", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressDialog.dismiss();
                    t.printStackTrace();
                    Toast.makeText(PaymentActivity.this, "Server connect error", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
