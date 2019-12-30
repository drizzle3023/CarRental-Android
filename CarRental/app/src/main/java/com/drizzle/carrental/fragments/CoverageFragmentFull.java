package com.drizzle.carrental.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.drizzle.carrental.R;
import com.drizzle.carrental.activities.AddCoverageActivity;
import com.drizzle.carrental.activities.ClaimsActivity;
import com.drizzle.carrental.api.ApiClient;
import com.drizzle.carrental.api.ApiInterface;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.serializers.ParseCompany;
import com.drizzle.carrental.serializers.ParseCoverage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoverageFragmentFull extends Fragment implements View.OnClickListener {

    private ImageButton buttonStartCoverage;
    private Button buttonClaims;


    private void getControlHandlersAndLinkActions(View view) {

        buttonStartCoverage = view.findViewById(R.id.button_start_coverage);
        buttonClaims = view.findViewById(R.id.button_claims);

        buttonStartCoverage.setOnClickListener(this);
        buttonClaims.setOnClickListener(this);

    }

    private void updateView() {

    }

    private void initVariables() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_coverage_full, container, false);

        getControlHandlersAndLinkActions(view);

        initVariables();

        updateView();

        getActiveCoverage();

        return view;
    }

    private void getActiveCoverage() {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("access_token", Globals.AccessToken);

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.getActiveCoverage(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    progressDialog.dismiss();

                    try {
                        JSONObject object = new JSONObject(response.body().string());

                        if (object.getString("success").equals("true")){

                            JSONObject data = object.getJSONObject("data");
                            JSONObject jsonCoverage = data.getJSONObject("coverage");

                            ParseCoverage parseCoverage = new Gson().fromJson(jsonCoverage.toString(), new TypeToken<ParseCoverage>() {}.getType());

                            // Parse company object from json object
                            JSONObject company = parseCoverage.getCompany();
                            ParseCompany parseCompany = new Gson().fromJson(company.toString(), new TypeToken<ParseCompany>() {}.getType());

                            // Set every values of this serializer to the coverage model.
                            Toast.makeText(getContext(), "Coverage Name: " + parseCoverage.getName() + "\nCompany Name: " + parseCompany.getName(), Toast.LENGTH_SHORT).show();

                        } else{
                            JSONObject data = object.getJSONObject("data");
                            Toast.makeText(getContext(), data.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Server connect error", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressDialog.dismiss();
                    t.printStackTrace();
                    Toast.makeText(getContext(), "Server connect error", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.button_start_coverage:
                navigateToAddCoverageActivity();
                break;
            case R.id.button_claims:
                navigateToClaimsActivity();
                break;
        }
    }

    /**
     * navigateToAddCoverageActivity
     */
    private void navigateToAddCoverageActivity() {
        Intent intent = new Intent(getActivity(), AddCoverageActivity.class);
        startActivity(intent);
    }

    /**
     * navigateToClaimsActivity
     */
    private void navigateToClaimsActivity() {
        Intent intent = new Intent(getActivity(), ClaimsActivity.class);
        startActivity(intent);
    }
}

