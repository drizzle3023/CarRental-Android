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
import com.drizzle.carrental.globals.Globals;
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
    public CallResult makePaymentsCall(JSONObject jsonObject) {

        Log.i("make_payments_call", "xxx" + jsonObject.toString());

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("access_token", "bstohcty6u56epm09pnplrlcgpv07dj6ur6korqomx2nk0lmcy8w97anye3pxj7xoey46ckmabnp7pht3t92ssgaoy5t007ojy557aaoimc2yw25tg2ke314bdw5w6m4");
            paramObject.put("paymentComponentData", jsonObject);
            paramObject.put("payment_id", Globals.paymentId);

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            Call<ResponseBody> callSync = apiInterface.doPayment(gsonObject);
            Response<ResponseBody> response = callSync.execute();

            JSONObject object = new JSONObject(response.body().string());

            JSONObject data = object.getJSONObject("data");

            if (object.getString("success").equalsIgnoreCase("true")){

                try {
                    JSONObject action = data.getJSONObject("action");

                    if (action != null){
                        return new CallResult(CallResult.ResultType.ACTION, action.toString());
                    } else {
                        String resultCode = data.getString("resultCode");
                        return new CallResult(CallResult.ResultType.FINISHED, resultCode);
                    }
                } catch (Exception e){

                    String resultCode = data.getString("resultCode");
                    return new CallResult(CallResult.ResultType.FINISHED, resultCode);
                }

            } else
                return new CallResult(CallResult.ResultType.ERROR_WITH_MESSAGE, data.getString("message"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new CallResult(CallResult.ResultType.ERROR, "Error");
    }

    public CallResult goToReturn(JSONObject jsonObject){

        Log.i("go_to_return", "xxx" + jsonObject.toString());

        return new CallResult(CallResult.ResultType.ACTION, jsonObject.toString());
    }

    @Override
    public CallResult makeDetailsCall(JSONObject jsonObject) {

        Log.i("make_details_call", "xxx" + jsonObject.toString());
        return new CallResult(CallResult.ResultType.FINISHED, "Authorised");
    }
}
