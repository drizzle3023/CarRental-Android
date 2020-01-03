package com.drizzle.carrental.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
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
import com.drizzle.carrental.enumerators.ClaimState;
import com.drizzle.carrental.enumerators.CoverageState;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.globals.SharedHelper;
import com.drizzle.carrental.models.Company;
import com.drizzle.carrental.models.Coverage;
import com.drizzle.carrental.serializers.ParseCompany;
import com.drizzle.carrental.serializers.ParseCoverage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.GregorianCalendar;

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

        if (Globals.coverage.isActiveState()) {

            buttonClaims.setVisibility(View.INVISIBLE);
        }
        else {

        }
    }

    private void initVariables() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_coverage_full, container, false);

        getControlHandlersAndLinkActions(view);

        initVariables();



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

            paramObject.put("access_token", SharedHelper.getKey(getActivity(), "access_token"));

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

                            Coverage coverage = new Coverage();
                            coverage.setTitle(parseCoverage.getName());
                            coverage.setState(CoverageState.values()[parseCoverage.getState()]);

                            JSONObject companyObject = parseCoverage.getCompany();
                            Company company = new Company();
                            company.setId(companyObject.getLong("id"));
                            company.setName(companyObject.getString("name"));
                            company.setType(companyObject.getString("type"));
                            coverage.setCompany(company);

                            GregorianCalendar dateFrom = new GregorianCalendar();
                            dateFrom.setTimeInMillis((long)parseCoverage.getStartAt() * 1000);

                            GregorianCalendar dateTo = new GregorianCalendar();
                            dateTo.setTimeInMillis((long)parseCoverage.getEndAt() * 1000);


                            Location location = new Location("location");
                            location.setLatitude(parseCoverage.getLatitude());
                            location.setLongitude(parseCoverage.getLongitude());

                            coverage.setLocationAddress(parseCoverage.getAddress());
                            coverage.setUrlVehicle(parseCoverage.getVideoVehicle());
                            coverage.setUrlMile(parseCoverage.getVideoMile());
                            coverage.setClaimCount(parseCoverage.getClaimCount());

                            if (coverage.getState() == CoverageState.COVERED) {

                                coverage.setActiveState(true);
                            }
                            else {
                                coverage.setActiveState(false);
                            }

                            updateView();
                            Globals.coverage = coverage;

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

