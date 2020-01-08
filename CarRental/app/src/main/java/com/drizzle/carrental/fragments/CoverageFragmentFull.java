package com.drizzle.carrental.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.drizzle.carrental.R;
import com.drizzle.carrental.activities.AddCoverageActivity;
import com.drizzle.carrental.activities.ClaimsActivity;
import com.drizzle.carrental.activities.HomeActivity;
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

import java.util.Calendar;
import java.util.GregorianCalendar;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoverageFragmentFull extends Fragment implements View.OnClickListener {

    private ImageButton buttonStartCoverage;
    private Button buttonClaims;
    private Button buttonGotIt;

    private TextView textViewCoverageTitle;
    private TextView textViewCoverageLocation;
    private TextView textViewCoveragePeriod;
    private LinearLayout layoutPeriod;
    private LinearLayout layoutLocation;

    private ImageButton imageButtonAssistence;
    private TextView textViewAssistence;

    private ImageButton imageButtonLostKeys;
    private TextView textViewLostKeys;

    private ImageButton imageButtonBrokenGlasses;
    private TextView textViewBrokenGlasses;

    private ImageButton imageButtonCoverTheft;
    private TextView textViewCoverTheft;

    private ImageView imageViewLocationIcon;

    private void getControlHandlersAndLinkActions(View view) {

        buttonStartCoverage = view.findViewById(R.id.button_start_coverage);
        buttonClaims = view.findViewById(R.id.button_claims);


        textViewCoverageTitle = view.findViewById(R.id.textview_title);
        textViewCoverageLocation = view.findViewById(R.id.textview_address);
        textViewCoveragePeriod = view.findViewById(R.id.textview_period);

        layoutPeriod = view.findViewById(R.id.layout_period);
        layoutLocation = view.findViewById(R.id.layout_location);

        buttonGotIt = view.findViewById(R.id.button_got_it);

        imageButtonAssistence = view.findViewById(R.id.imagebutton_assistence);
        textViewAssistence = view.findViewById(R.id.textview_assistence);

        imageButtonBrokenGlasses = view.findViewById(R.id.imagebutton_broken_glasses);
        textViewBrokenGlasses = view.findViewById(R.id.textview_broken_glasses);

        imageButtonLostKeys = view.findViewById(R.id.imagebutton_lostkeys);
        textViewLostKeys = view.findViewById(R.id.textview_lostkeys);

        imageButtonCoverTheft = view.findViewById(R.id.imagebutton_cover_theft);
        textViewCoverTheft = view.findViewById(R.id.textview_cover_theft);

        imageViewLocationIcon = view.findViewById(R.id.imageview_location_icon);

        buttonStartCoverage.setOnClickListener(this);
        buttonClaims.setOnClickListener(this);
        buttonGotIt.setOnClickListener(this);

    }

    private void updateView() {

        if (Globals.coverage != null && Globals.coverage.getId() != null && Globals.coverage.getId() > 0) {

            buttonClaims.setVisibility(View.VISIBLE);

            buttonStartCoverage.setImageResource(R.drawable.covered_coverage_image);

            textViewCoverageTitle.setText(Globals.coverage.getTitle());
            textViewCoverageLocation.setText(Globals.coverage.getLocationAddress());
            textViewCoveragePeriod.setText(Globals.coverage.getRemainingTime());
            buttonGotIt.setVisibility(View.GONE);
            imageViewLocationIcon.setVisibility(View.VISIBLE);
            layoutPeriod.setVisibility(View.VISIBLE);

            textViewAssistence.setTextColor(getResources().getColor(R.color.colorNormalText, null));
            textViewLostKeys.setTextColor(getResources().getColor(R.color.colorInvalid, null));
            textViewBrokenGlasses.setTextColor(getResources().getColor(R.color.colorInvalid, null));
            textViewCoverTheft.setTextColor(getResources().getColor(R.color.colorInvalid, null));

        } else {
            buttonClaims.setVisibility(View.INVISIBLE);
            buttonGotIt.setVisibility(View.GONE);
            layoutPeriod.setVisibility(View.GONE);
            imageViewLocationIcon.setVisibility(View.GONE);

            if (Globals.coverage.getState() == CoverageState.CANCELLED) {

                buttonStartCoverage.setImageResource(R.drawable.icon_add_coverage);
            } else if (Globals.coverage.getState() == CoverageState.EXPIRED) {

                buttonStartCoverage.setImageResource(R.drawable.video_vehicle);
                buttonStartCoverage.setAlpha(	0.25f);

                buttonGotIt.setVisibility(View.VISIBLE);
                textViewCoverageTitle.setText(R.string.coverage_expired_title);
                layoutLocation.setVisibility(View.GONE);
                imageViewLocationIcon.setVisibility(View.GONE);
                layoutPeriod.setVisibility(View.VISIBLE);
                textViewCoveragePeriod.setText(R.string.no_remainingtime);
                buttonStartCoverage.setImageResource(R.drawable.covered_coverage_image);

                imageButtonAssistence.setImageResource(R.drawable.assitence_disabled);
                imageButtonLostKeys.setImageResource(R.drawable.lost_keys_disabled);
                imageButtonBrokenGlasses.setImageResource(R.drawable.lost_keys_disabled);
                imageButtonCoverTheft.setImageResource(R.drawable.lost_keys_disabled);

                textViewAssistence.setTextColor(getResources().getColor(R.color.colorInvalid, null));
                textViewLostKeys.setTextColor(getResources().getColor(R.color.colorInvalid, null));
                textViewBrokenGlasses.setTextColor(getResources().getColor(R.color.colorInvalid, null));
                textViewCoverTheft.setTextColor(getResources().getColor(R.color.colorInvalid, null));

            } else {

                buttonStartCoverage.setImageResource(R.drawable.icon_add_coverage);

                imageButtonAssistence.setImageResource(R.drawable.assitence_disabled);
                imageButtonLostKeys.setImageResource(R.drawable.lost_keys_disabled);
                imageButtonBrokenGlasses.setImageResource(R.drawable.lost_keys_disabled);
                imageButtonCoverTheft.setImageResource(R.drawable.lost_keys_disabled);

            }
        }

        updateBottomBar();
    }

    private void updateBottomBar() {

        if (Globals.coverage != null && Globals.coverage.getId() != null && Globals.coverage.getId() > 0) {

            imageButtonAssistence.setImageResource(R.drawable.assitence_active);
            imageButtonLostKeys.setImageResource(R.drawable.lost_keys_enabled);
            imageButtonBrokenGlasses.setImageResource(R.drawable.lost_keys_enabled);
            imageButtonCoverTheft.setImageResource(R.drawable.lost_keys_enabled);

            textViewAssistence.setTextColor(getResources().getColor(R.color.colorNormalText, null));
            textViewLostKeys.setTextColor(getResources().getColor(R.color.colorInvalid, null));
            textViewBrokenGlasses.setTextColor(getResources().getColor(R.color.colorInvalid, null));
            textViewCoverTheft.setTextColor(getResources().getColor(R.color.colorInvalid, null));

        } else {

            if (Globals.coverage.getState() == CoverageState.EXPIRED) {




                imageButtonAssistence.setImageResource(R.drawable.assitence_disabled);
                imageButtonLostKeys.setImageResource(R.drawable.lost_keys_disabled);
                imageButtonBrokenGlasses.setImageResource(R.drawable.lost_keys_disabled);
                imageButtonCoverTheft.setImageResource(R.drawable.lost_keys_disabled);

                textViewAssistence.setTextColor(getResources().getColor(R.color.colorInvalid, null));
                textViewLostKeys.setTextColor(getResources().getColor(R.color.colorInvalid, null));
                textViewBrokenGlasses.setTextColor(getResources().getColor(R.color.colorInvalid, null));
                textViewCoverTheft.setTextColor(getResources().getColor(R.color.colorInvalid, null));

            } else {

                buttonStartCoverage.setImageResource(R.drawable.icon_add_coverage);
                imageButtonAssistence.setImageResource(R.drawable.assitence_disabled);
                imageButtonLostKeys.setImageResource(R.drawable.lost_keys_disabled);
                imageButtonBrokenGlasses.setImageResource(R.drawable.lost_keys_disabled);
                imageButtonCoverTheft.setImageResource(R.drawable.lost_keys_disabled);

            }
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

                        if (object.getString("success").equals("true")) {

                            JSONObject data = object.getJSONObject("data");
                            JSONObject jsonCoverage = data.getJSONObject("coverage");

                            ParseCoverage parseCoverage = new Gson().fromJson(jsonCoverage.toString(), new TypeToken<ParseCoverage>() {
                            }.getType());

                            Coverage coverage = new Coverage();
                            coverage.setId((long) parseCoverage.getId());
                            coverage.setTitle(parseCoverage.getName());

                            coverage.setState(CoverageState.values()[parseCoverage.getState() - 1]);

                            JSONObject companyObject = parseCoverage.getCompany();
                            Company company = new Company();
//                            company.setId(companyObject.getLong("id"));
//                            company.setName(companyObject.getString("name"));
//                            company.setType(companyObject.getString("type"));
                            coverage.setCompany(company);


                            GregorianCalendar dateFrom = new GregorianCalendar();
                            dateFrom.setTimeInMillis((long) parseCoverage.getStartAt() * 1000);


                            GregorianCalendar dateTo = new GregorianCalendar();
                            dateTo.setTimeInMillis((long) parseCoverage.getEndAt() * 1000);

                            coverage.setDateFrom(dateFrom);
                            coverage.setDateTo(dateTo);

                            Location location = new Location("location");
                            location.setLatitude(parseCoverage.getLatitude());
                            location.setLongitude(parseCoverage.getLongitude());

                            coverage.setLocationAddress(parseCoverage.getAddress());
                            coverage.setUrlVehicle(parseCoverage.getVideoVehicle());
                            coverage.setUrlMile(parseCoverage.getVideoMile());
                            coverage.setClaimCount(parseCoverage.getClaimCount());

                            if (coverage.getState() == CoverageState.COVERED) {

                                coverage.setActiveState(true);
                            } else {
                                coverage.setActiveState(false);
                            }


                            Globals.coverage = coverage;

                        } else {
                            JSONObject data = object.getJSONObject("data");
                            //Toast.makeText(getContext(), data.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Server connect error", Toast.LENGTH_SHORT).show();
                    }

                    updateView();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressDialog.dismiss();
                    t.printStackTrace();
                    Toast.makeText(getContext(), "Server connect error", Toast.LENGTH_SHORT).show();

                    updateView();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onResume() {

        getActiveCoverage();

        super.onResume();


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.button_start_coverage:
                if (Globals.coverage.getId() != null && Globals.coverage.getId() > 0 && Globals.coverage.isActiveState()) {
                    Toast.makeText(getActivity(), "Coverage already exists.", Toast.LENGTH_SHORT).show();
                } else {
                    navigateToAddCoverageActivity();
                }

                break;

            case R.id.button_got_it:
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
        startActivityForResult(intent, HomeActivity.ADD_COVERAGE_ACTIVITY_REQUEST);
    }

    /**
     * navigateToClaimsActivity
     */
    private void navigateToClaimsActivity() {
        Intent intent = new Intent(getActivity(), ClaimsActivity.class);
        startActivity(intent);
    }
}

