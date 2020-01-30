package com.drizzle.carrental.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.drizzle.carrental.R;
import com.drizzle.carrental.activities.AddCoverageActivity;
import com.drizzle.carrental.activities.ClaimsActivity;
import com.drizzle.carrental.activities.HomeActivity;
import com.drizzle.carrental.activities.PaymentActivity;
import com.drizzle.carrental.activities.SubscriptionNewActivity;
import com.drizzle.carrental.api.ApiClient;
import com.drizzle.carrental.api.ApiInterface;
import com.drizzle.carrental.customcomponents.AppCompatImageView_Round_55;
import com.drizzle.carrental.enumerators.CoverageState;
import com.drizzle.carrental.globals.Constants;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.globals.SharedHelper;
import com.drizzle.carrental.models.Company;
import com.drizzle.carrental.models.Coverage;
import com.drizzle.carrental.serializers.ParseCoverage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.GregorianCalendar;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoverageFragmentFull extends Fragment implements View.OnClickListener {

    private AppCompatImageView_Round_55 imageButtonStartCoverage;
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

    private ImageButton imageButtonRemoveCoverage;

    private boolean firstFlag = true;

    int payState= 0;

    private void getControlHandlersAndLinkActions(View view) {

        imageButtonStartCoverage = view.findViewById(R.id.imageview_start_coverage);
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

        imageButtonRemoveCoverage = view.findViewById(R.id.imagebutton_remove_coverage);
        imageButtonRemoveCoverage.setOnClickListener(this);

        imageButtonStartCoverage.setOnClickListener(this);
        buttonClaims.setOnClickListener(this);
        buttonGotIt.setOnClickListener(this);

    }

    private void resetView() {

        buttonClaims.setVisibility(View.GONE);
        imageButtonRemoveCoverage.setVisibility(View.GONE);
        textViewCoverageTitle.setVisibility(View.VISIBLE);
        imageViewLocationIcon.setVisibility(View.GONE);
        textViewCoverageLocation.setVisibility(View.VISIBLE);
        layoutPeriod.setVisibility(View.GONE);
        buttonGotIt.setVisibility(View.GONE);
        layoutLocation.setVisibility(View.VISIBLE);
        imageButtonStartCoverage.setAlpha(1f);

        imageButtonStartCoverage.setImageResource(R.drawable.ic_icon_add_coverage);
        textViewCoverageTitle.setText(getResources().getString(R.string.start_coverage_title));
        textViewCoverageLocation.setText(getResources().getString(R.string.have_unlimited_coverage));

    }

    private void updateView() {


        if (Globals.coverage != null) { //coverage does exist

            if (Globals.coverage.getState() != null) { //coverage state exist

                switch (Globals.coverage.getState()) {

                    case UNCOVERED:

                        buttonClaims.setVisibility(View.GONE);
                        imageButtonRemoveCoverage.setVisibility(View.VISIBLE);
                        textViewCoverageTitle.setVisibility(View.VISIBLE);
                        imageViewLocationIcon.setVisibility(View.VISIBLE);
                        textViewCoverageLocation.setVisibility(View.VISIBLE);
                        layoutPeriod.setVisibility(View.VISIBLE);
                        buttonGotIt.setVisibility(View.GONE);
                        layoutLocation.setVisibility(View.VISIBLE);
                        imageButtonStartCoverage.setAlpha(1f);

                        if (Globals.coverage.getUrlImageVehicle() != null && !Globals.coverage.getUrlImageVehicle().isEmpty()) {

                            Picasso picasso = Picasso.get();
                            picasso.invalidate(Globals.coverage.getUrlImageVehicle());
                            picasso.load(Globals.coverage.getUrlImageVehicle()).resize(imageButtonStartCoverage.getWidth(), imageButtonStartCoverage.getHeight()).placeholder(R.drawable.ic_icon_add_coverage).into(imageButtonStartCoverage);
                        }
                        if (Globals.coverage.getTitle() != null && !Globals.coverage.getTitle().isEmpty()) {
                            textViewCoverageTitle.setText(Globals.coverage.getTitle());
                        }
                        if (Globals.coverage.getLocationAddress() != null && !Globals.coverage.getLocationAddress().isEmpty()) {
                            textViewCoverageLocation.setText(Globals.coverage.getLocationAddress());
                        }
                        if (Globals.coverage.getRemainingTimeAsString() != null && !Globals.coverage.getRemainingTimeAsString().isEmpty()) {
                            textViewCoveragePeriod.setText(Globals.coverage.getRemainingTimeAsString());
                        }

                        break;
                    case COVERED:

                        buttonClaims.setVisibility(View.VISIBLE);
                        imageButtonRemoveCoverage.setVisibility(View.VISIBLE);
                        textViewCoverageTitle.setVisibility(View.VISIBLE);
                        imageViewLocationIcon.setVisibility(View.VISIBLE);
                        textViewCoverageLocation.setVisibility(View.VISIBLE);
                        layoutPeriod.setVisibility(View.VISIBLE);
                        buttonGotIt.setVisibility(View.GONE);
                        layoutLocation.setVisibility(View.VISIBLE);
                        imageButtonStartCoverage.setAlpha(1f);

                        if (Globals.coverage.getUrlImageVehicle() != null && !Globals.coverage.getUrlImageVehicle().isEmpty()) {
                            Picasso picasso = Picasso.get();
                            picasso.invalidate(Globals.coverage.getUrlImageVehicle());
                            picasso.load(Globals.coverage.getUrlImageVehicle()).resize(imageButtonStartCoverage.getWidth(), imageButtonStartCoverage.getHeight()).placeholder(R.drawable.ic_icon_add_coverage).into(imageButtonStartCoverage);
                        }
                        if (Globals.coverage.getTitle() != null && !Globals.coverage.getTitle().isEmpty()) {
                            textViewCoverageTitle.setText(Globals.coverage.getTitle());
                        }
                        if (Globals.coverage.getLocationAddress() != null && !Globals.coverage.getLocationAddress().isEmpty()) {
                            textViewCoverageLocation.setText(Globals.coverage.getLocationAddress());
                        }
                        if (Globals.coverage.getRemainingTimeAsString() != null && !Globals.coverage.getRemainingTimeAsString().isEmpty()) {
                            textViewCoveragePeriod.setText(Globals.coverage.getRemainingTimeAsString());
                        }

                        break;

                    case EXPIRED:

                        buttonClaims.setVisibility(View.GONE);
                        imageButtonRemoveCoverage.setVisibility(View.GONE);
                        textViewCoverageTitle.setVisibility(View.VISIBLE);
                        imageViewLocationIcon.setVisibility(View.GONE);
                        textViewCoverageLocation.setVisibility(View.VISIBLE);
                        layoutPeriod.setVisibility(View.VISIBLE);
                        buttonGotIt.setVisibility(View.VISIBLE);
                        layoutLocation.setVisibility(View.GONE);

                        imageButtonStartCoverage.setImageResource(R.drawable.covered_coverage_image);

                        Picasso picasso = Picasso.get();
                        picasso.invalidate(Globals.coverage.getUrlImageVehicle());
                        picasso.load(Globals.coverage.getUrlImageVehicle()).placeholder(R.drawable.covered_coverage_image).into(imageButtonStartCoverage);
                        imageButtonStartCoverage.setAlpha(0.25f);
                        textViewCoverageTitle.setText(R.string.coverage_expired_title);
                        textViewCoveragePeriod.setText(R.string.no_remainingtime);

                        break;
                    case CANCELLED:
                    default:

                        resetView();

                        break;

                }

            } else { //coverage state doesn't exist
                resetView();

            }
        } else { //coverage doesn't exist
            resetView();
        }

        updateBottomBar();
    }

    private void resetBottomBar() {

        imageButtonAssistence.setColorFilter(getResources().getColor(R.color.colorBottombarIconDisabled, null));
        imageButtonAssistence.setBackgroundResource(R.drawable.coverage_bottom_bar_icon_radius_inactive);

        imageButtonLostKeys.setImageResource(R.drawable.lost_keys_disabled);
        imageButtonLostKeys.setBackgroundResource(R.drawable.coverage_bottom_bar_icon_radius_inactive);

        imageButtonBrokenGlasses.setImageResource(R.drawable.lost_keys_disabled);
        imageButtonBrokenGlasses.setBackgroundResource(R.drawable.coverage_bottom_bar_icon_radius_inactive);

        imageButtonCoverTheft.setImageResource(R.drawable.lost_keys_disabled);
        imageButtonCoverTheft.setBackgroundResource(R.drawable.coverage_bottom_bar_icon_radius_inactive);

        textViewAssistence.setTextColor(getResources().getColor(R.color.colorInvalid, null));
        textViewLostKeys.setTextColor(getResources().getColor(R.color.colorInvalid, null));
        textViewBrokenGlasses.setTextColor(getResources().getColor(R.color.colorInvalid, null));
        textViewCoverTheft.setTextColor(getResources().getColor(R.color.colorInvalid, null));
    }

    private void updateBottomBar() {

        if (Globals.coverage != null) { //coverage does exist

            if (Globals.coverage.getState() != null) { //coverage state exist

                switch (Globals.coverage.getState()) {


                    case COVERED:


                        imageButtonAssistence.setColorFilter(Color.WHITE);
                        imageButtonAssistence.setBackgroundResource(R.drawable.coverage_bottom_bar_icon_radius_active);

                        imageButtonLostKeys.setColorFilter(getResources().getColor(R.color.colorNormalBlue, null));

                        imageButtonBrokenGlasses.setColorFilter(getResources().getColor(R.color.colorNormalBlue, null));

                        imageButtonCoverTheft.setColorFilter(getResources().getColor(R.color.colorNormalBlue, null));


                        textViewAssistence.setTextColor(getResources().getColor(R.color.colorNormalText, null));
                        textViewLostKeys.setTextColor(getResources().getColor(R.color.colorInvalid, null));
                        textViewBrokenGlasses.setTextColor(getResources().getColor(R.color.colorInvalid, null));
                        textViewCoverTheft.setTextColor(getResources().getColor(R.color.colorInvalid, null));

                        break;
                    case UNCOVERED:
                    case EXPIRED:
                    case CANCELLED:
                    default:
                        resetBottomBar();
                        break;
                }

            } else { //coverage state doesn't exist
                resetBottomBar();

            }
        } else { //coverage doesn't exist
            resetBottomBar();
        }

    }

    private void initVariables() {

        payState = 0;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_coverage_full, container, false);

        getControlHandlersAndLinkActions(view);

        initVariables();


            //getActiveCoverage();


        return view;
    }

    private void getActiveCoverage() {

        firstFlag = false;
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

                    firstFlag = true;
                    progressDialog.dismiss();

                    try {
                        JSONObject object = new JSONObject(response.body().string());


                        try {
                            payState = Integer.parseInt(object.getJSONObject("data").getString("pay_state"));
                        } catch (Exception e) {
                            e.printStackTrace();
                            payState = 0;
                        }

                        if (payState == 0) {
                            navigateToSubscribeActivity();
                        }
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
                            company.setName(parseCoverage.getName());
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

                            if (!parseCoverage.getVideoVehicle().isEmpty()) {
                                coverage.setUrlVideoVehicle(Constants.MEDIA_PATH_URL + parseCoverage.getVideoVehicle());

                            }

                            if (!parseCoverage.getImageVehicle().isEmpty()) {
                                coverage.setUrlImageVehicle(Constants.MEDIA_PATH_URL + parseCoverage.getImageVehicle());
                            }

                            if (!parseCoverage.getVideoMile().isEmpty()) {
                                coverage.setUrlVideoMile(Constants.MEDIA_PATH_URL + parseCoverage.getVideoMile());
                            }

                            if (!parseCoverage.getImageMile().isEmpty()) {
                                coverage.setUrlImageMile(Constants.MEDIA_PATH_URL + parseCoverage.getImageMile());
                            }

                            coverage.setClaimCount(parseCoverage.getClaimCount());

                            if (coverage.getState() == CoverageState.COVERED) {

                                coverage.setActiveState(true);
                            } else {
                                coverage.setActiveState(false);
                            }

                            coverage.setRemainingTime(parseCoverage.getRemainingTime());

                            Globals.coverage = coverage;

                        } else {
                            JSONObject data = object.getJSONObject("data");
                            //Toast.makeText(getContext(), data.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (
                            Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Server connect error", Toast.LENGTH_SHORT).show();
                    }

                    updateView();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    firstFlag = true;
                    progressDialog.dismiss();
                    t.printStackTrace();
                    Toast.makeText(getContext(), "Server connect error", Toast.LENGTH_SHORT).show();

                    updateView();
                }
            });

        } catch (
                Exception e) {
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

            case R.id.imageview_start_coverage:

                if (payState == 0) {
                    navigateToSubscribeActivity();
                    break;
                }


                if (Globals.coverage == null) {
                    navigateToAddCoverageActivity();
                } else if (Globals.coverage.getId() != null && Globals.coverage.getId() > 0 && Globals.coverage.getState() == CoverageState.COVERED) {
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

            case R.id.imagebutton_remove_coverage:
                new AlertDialog.Builder(getActivity())
                        .setTitle("Cancel Coverage")
                        .setMessage("Are you sure you want to cancel current coverage?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                cancelCoverage();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();

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

    private void navigateToSubscribeActivity() {

        if (Globals.profile.getVehicleType() != null && Globals.profile.getWorldZone() != null && !Globals.profile.getWorldZone().isEmpty()) {
            Intent intent = new Intent(getActivity(), PaymentActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
        else {
            Intent intent = new Intent(getActivity(), SubscriptionNewActivity.class);
            startActivity(intent);
            getActivity().finish();
        }



    }

    private void cancelCoverage() {

        if (Globals.coverage == null) {
            Globals.coverage = new Coverage();
            updateView();
            return;
        }
        if (Globals.coverage.getId() == null) {
            Globals.coverage = new Coverage();
            updateView();
            return;
        }
        if (Globals.coverage.getId() < 1) {
            Globals.coverage = new Coverage();
            updateView();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("access_token", SharedHelper.getKey(getActivity(), "access_token"));
            paramObject.put("coverage_id", Globals.coverage.getId());

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.cancelCoverage(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    progressDialog.dismiss();

                    try {
                        JSONObject object = new JSONObject(response.body().string());

                        if (object.getString("success").equals("true")) {

                            Globals.coverage = new Coverage();
                            updateView();

                        } else {

                            JSONObject data = object.getJSONObject("data");
                            Toast.makeText(getContext(), data.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (
                            Exception e) {
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

        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }
}

