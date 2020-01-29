package com.drizzle.carrental.activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.adyen.checkout.base.model.PaymentMethodsApiResponse;
import com.adyen.checkout.card.CardConfiguration;
import com.adyen.checkout.core.api.Environment;
import com.adyen.checkout.dropin.DropIn;
import com.adyen.checkout.dropin.DropInConfiguration;
import com.drizzle.carrental.R;
import com.drizzle.carrental.api.ApiClient;
import com.drizzle.carrental.api.ApiInterface;
import com.drizzle.carrental.globals.Constants;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.globals.SharedHelper;
import com.drizzle.carrental.globals.Utils;
import com.drizzle.carrental.services.YourDropService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends FragmentActivity {

    private Button buttonPay;

    private TextView textViewPricePerYear;

    private TextView textViewRentalType;

    private TextView textViewVehicleType;

    private TextView textViewWorldZone;

    private TextView textViewExpireDate;

    private ImageButton buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        textViewRentalType = findViewById(R.id.textview_rental_type);
        textViewVehicleType = findViewById(R.id.textview_vehicle_type);
        textViewWorldZone = findViewById(R.id.textview_worldzone);
        textViewPricePerYear = findViewById(R.id.textview_price_per_year);
        textViewExpireDate = findViewById(R.id.textview_expire_date);

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

        buttonBack = findViewById(R.id.imagebutton_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
        initView();
    }


    private void initView() {

        if (Globals.selectedVehicleType == null || Globals.selectedServiceArea == null) {
            return;
        }

        double amount = 0;


        String currency = "";

        if (Globals.profile.getWorldZone() != null && !Globals.profile.getWorldZone().isEmpty()) {

            if (Globals.profile.getWorldZone().equals("US")) {

                currency = Constants.CURRENCY_USD;
                amount = Globals.selectedVehicleType.getPricePerYearUsd();
            }
            else if (Globals.profile.getWorldZone().equals("EU")) {

                currency = Constants.CURRENCY_EURO;
                amount = Globals.selectedVehicleType.getPricePerYearEur();
            }
        }

        String strAmount = String.format(Locale.getDefault(),  "%.2f", amount);
        if (currency.equals("EUR")) {
            currency = getResources().getString(R.string.euro_character);
        }
        else if (currency.equals("USD")) {
            currency = getResources().getString(R.string.usd_character);
        }
        String paymentInformation = strAmount + " " + currency + " / per year";
        textViewPricePerYear.setText(paymentInformation);

        textViewRentalType.setText(getText(R.string.rental_type_unlimited_rental));
        textViewVehicleType.setText("Vehicle - " + Globals.selectedVehicleType.getName());
        textViewWorldZone.setText("World zone - " + Globals.selectedServiceArea.getAreaName());

        buttonPay.setText("Pay " + strAmount + " " + currency);
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.add(Calendar.YEAR, 1);

        String strExpireDate = "";

        DateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_GENERAL);
            strExpireDate = df.format(calendar.getTime());


            String strPrefix = "Covered until ";
        strExpireDate = strPrefix + strExpireDate;

        Spannable spannable = new SpannableString(strExpireDate);

        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorNormalBlue, null)), 0, strPrefix.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        textViewExpireDate.setText(spannable, TextView.BufferType.SPANNABLE);

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

            paramObject.put("access_token", SharedHelper.getKey(this, "access_token"));
            paramObject.put("car_type_id", Globals.selectedVehicleType.getId());

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

                            if (data.getString("token_state").equals("valid")) {

                                Iterator<String> keys = object.getJSONObject("data").keys();

                                for (Iterator i = keys; i.hasNext(); ) {

                                    if (i.next().equals("refresh_token")) {
                                        String newPayload = data.get("refresh_token").toString();
                                        String newToken = data.getString("access_token");

                                        SharedHelper.putKey(PaymentActivity.this, "access_token", newToken);
                                        SharedHelper.putKey(PaymentActivity.this, "payload", newPayload);

                                        Utils.initHabitSDK(PaymentActivity.this);
                                    }
                                }
                            }
                        } else {

                            JSONObject data = object.getJSONObject("data");
                            Toast.makeText(PaymentActivity.this, data.getString("message"), Toast.LENGTH_SHORT).show();

                            if (object.getString("token_state").equals("invalid")) {

                                Utils.logout(PaymentActivity.this, PaymentActivity.this);
                            }

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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
