package com.drizzle.carrental.services;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.adyen.checkout.dropin.service.CallResult;
import com.adyen.checkout.dropin.service.DropInService;
import com.drizzle.carrental.api.ApiClient;
import com.drizzle.carrental.api.ApiInterface;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YourDropService extends DropInService {

    @Override
    public CallResult makeDetailsCall(JSONObject jsonObject) {

        Log.i("make_details_call", "xxx" + jsonObject.toString());

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
//        try {
//            JSONObject paramObject = new JSONObject();
//
//            paramObject.put("access_token", "bstohcty6u56epm09pnplrlcgpv07dj6ur6korqomx2nk0lmcy8w97anye3pxj7xoey46ckmabnp7pht3t92ssgaoy5t007ojy557aaoimc2yw25tg2ke314bdw5w6m4");
//            paramObject.put("paymentComponentData", jsonObject);
//            JsonParser jsonParser = new JsonParser();
//            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());
//
//            apiInterface.getPaymentMethods(gsonObject).enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//
//                    progressDialog.dismiss();
//
//                    try {
//                        JSONObject object = new JSONObject(response.body().string());
//
//                        if (object.getString("success").equals("true")){
//
//                            return new CallResult(CallResult.ResultType.ACTION, jsonObject.toString());
//
//                        } else{
//                            JSONObject data = object.getJSONObject("data");
//                            Toast.makeText(YourDropService.this, data.getString("message"), Toast.LENGTH_SHORT).show();
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Toast.makeText(YourDropService.this, "Server connect error", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    progressDialog.dismiss();
//                    t.printStackTrace();
//                    Toast.makeText(YourDropService.this, "Server connect error", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        return null;

    }

    @Override
    public CallResult makePaymentsCall(JSONObject jsonObject) {

        Log.i("make_payments_call", "xxx" + jsonObject.toString());
        return new CallResult(CallResult.ResultType.FINISHED, "Authorised");
    }
}
