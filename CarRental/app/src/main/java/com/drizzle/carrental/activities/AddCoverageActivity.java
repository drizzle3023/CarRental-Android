package com.drizzle.carrental.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.drizzle.carrental.R;
import com.drizzle.carrental.api.VolleyMultipartRequest;
import com.drizzle.carrental.customcomponents.AppCompatImageView_Round_10;
import com.drizzle.carrental.enumerators.CoverageState;
import com.drizzle.carrental.globals.Constants;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.globals.SharedHelper;
import com.drizzle.carrental.globals.Utils;
import com.drizzle.carrental.models.Company;
import com.drizzle.carrental.models.Coverage;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class AddCoverageActivity extends Activity implements View.OnClickListener {


    static final int START_COVERAGE_ACTIVITY_REQUEST = 1;
    static final int RECORD_VEHICLE_ACTIVITY_REQUEST = 2;
    static final int RECORD_MILE_ACTIVITY_REQUEST = 3;

    /**
     * UI Control Handlers
     */
    private TextView captionStartCoverage;
    private Button buttonStartCoverage;
    private TextView captionRecordCar;
    private Button buttonRecordCar;
    private TextView captionRecordMile;
    private Button buttonRecordMile;

    private Button buttonGotit;
    private ImageButton buttonBack;

    private ImageButton buttonEdit;

    private LinearLayout layoutCoverage;
    private LinearLayout layoutCoverageLine;
    private LinearLayout layoutCoverageContent;
    private LinearLayout layoutCoverageLineHidden;

    private LinearLayout layoutVideoVehicle;
    private LinearLayout layoutVideoVehicleLine;
    private LinearLayout layoutVideoVehicleContent;
    private LinearLayout layoutVideoVehicleLineHiden;

    private LinearLayout layoutVideoMile;
    private LinearLayout layoutVideoMileLine;
    private LinearLayout layoutVideoMileContent;
    private LinearLayout layoutVideoMileLineHidden;

    private ImageButton imageButtonDeleteCoverage;
    private ImageButton imageButtonDeleteVideoVehicle;
    private ImageButton imageButtonDeleteVideoMile;

    private Button buttonLocation;
    private Button buttonCompany;
    private Button buttonPeriod;

    private AppCompatImageView_Round_10 imageViewVideoVehicle;
    private AppCompatImageView_Round_10 imageViewVideoMile;

    private Coverage coverage = new Coverage();

    /**
     * Temporary variables for state
     */
    public enum CoverageCurrentStep {

        NEW("NEW ", 0),
        COVERAGE_ADDED("COVERAGE_ADDED", 1),
        VIDEO_VEHICLE_ADDED("VIDEO_VEHICLE_ADDED", 2),
        COMPLETED("COMPLETED", 3);

        private String stringValue;
        private int intValue;

        private CoverageCurrentStep(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        public int getIntValue() {

            return intValue;
        }

        @Override
        public String toString() {
            return stringValue;
        }
    }

    private boolean isEditMode;
    private CoverageCurrentStep coverageCurrentStep = CoverageCurrentStep.NEW;

    /**
     * get control handlers by id and add listenres
     */
    private void getControlHandlersAndLinkActions() {

        buttonBack = findViewById(R.id.button_back_to_onboarding);
        captionStartCoverage = (TextView) findViewById(R.id.caption_start_coverage);
        buttonStartCoverage = findViewById(R.id.imagebutton_start_coverage);

        captionRecordCar = (TextView) findViewById(R.id.caption_record_car);
        buttonRecordCar = findViewById(R.id.button_record_car);

        captionRecordMile = (TextView) findViewById(R.id.caption_record_mile);
        buttonRecordMile = findViewById(R.id.button_record_mile);

        buttonGotit = (Button) findViewById(R.id.button_got_it);
        buttonEdit = (ImageButton) findViewById((R.id.button_edit));
        imageButtonDeleteCoverage = (ImageButton) findViewById(R.id.button_delete_coverage);
        imageButtonDeleteVideoVehicle = (ImageButton) findViewById(R.id.button_delete_video);
        imageButtonDeleteVideoMile = (ImageButton) findViewById((R.id.button_delete_miles));
        layoutCoverage = (LinearLayout) findViewById(R.id.layout_coverage);
        layoutCoverageLine = findViewById(R.id.layout_coverage_line);
        layoutCoverageContent = findViewById(R.id.layout_coverage_content);
        layoutCoverageLineHidden = findViewById(R.id.layout_coverage_line_hidden);

        layoutVideoVehicle = (LinearLayout) findViewById(R.id.layout_video_vehicle);
        layoutVideoVehicleLine = findViewById(R.id.layout_video_vehicle_line);
        layoutVideoVehicleContent = findViewById(R.id.layout_video_vehicle_content);
        layoutVideoVehicleLineHiden = findViewById(R.id.layout_video_vehicle_line_hidden);
        imageViewVideoVehicle = findViewById(R.id.imageview_video_vehicle);

        layoutVideoMile = findViewById(R.id.layout_video_mile);
        layoutVideoMileLine = findViewById(R.id.layout_video_mile_line);
        layoutVideoMileContent = findViewById(R.id.layout_video_mile_content);
        layoutVideoMileLineHidden = findViewById(R.id.layout_video_mile_line_hidden);
        imageViewVideoMile = findViewById(R.id.imageview_video_mile);

        buttonLocation = findViewById(R.id.button_location);
        buttonCompany = findViewById(R.id.button_company);
        buttonPeriod = findViewById(R.id.button_period);


        //bind listener
        captionStartCoverage.setOnClickListener(this);
        buttonStartCoverage.setOnClickListener(this);
        captionRecordCar.setOnClickListener(this);
        buttonRecordCar.setOnClickListener(this);
        captionRecordMile.setOnClickListener(this);
        buttonRecordMile.setOnClickListener(this);

        buttonEdit.setOnClickListener(this);
        imageButtonDeleteCoverage.setOnClickListener(this);
        imageButtonDeleteVideoVehicle.setOnClickListener(this);
        imageButtonDeleteVideoMile.setOnClickListener(this);

        buttonGotit.setOnClickListener(this);
        buttonBack.setOnClickListener(this);

    }

    /**
     * Init and fetch data from server
     */
    private void prepareTestData() {

        Coverage coverage = new Coverage();

        //fetch data from server
        Location location = new Location("CoverageLocation");
        location.setLatitude(35.6118677);
        location.setLongitude(139.6872165);

        Company company = new Company();
        company.setId((long) 1);
        company.setName("Budge Rental Car");
        company.setType("Airport");

        coverage.setLocation(location);
        coverage.setCompany(company);
        coverage.setLocationAddress("Queens, NY 11430, USA");
        coverage.setDateFrom(new GregorianCalendar(2019, 1, 1));
        coverage.setDateTo(new GregorianCalendar(2020, 1, 1));


        Globals.coverage = coverage;
        isEditMode = false;

    }

    /**
     * Update View according to current state
     */
    private void updateView() {

        if (coverageCurrentStep.equals(CoverageCurrentStep.NEW)) {

            captionStartCoverage.setBackgroundResource(R.drawable.coverage_add_button);
            captionRecordCar.setBackgroundResource(0);
            captionRecordMile.setBackgroundResource(0);
            buttonGotit.setEnabled(false);
            buttonGotit.setBackgroundResource(R.drawable.inactive_button);

            buttonStartCoverage.setBackgroundResource(R.drawable.add_coverage_step_button_back_active);
            buttonStartCoverage.setEnabled(true);

            buttonRecordCar.setBackgroundResource(R.drawable.add_coverage_step_button_back_inactive);
            buttonRecordCar.setEnabled(false);

            buttonRecordMile.setBackgroundResource(R.drawable.add_coverage_step_button_back_inactive);
            buttonRecordMile.setEnabled(false);

            captionStartCoverage.setTextColor(getColor(R.color.colorNormalBlue));
            captionRecordCar.setTextColor(getColor(R.color.colorAddCoverageStepTextViewInactive));
            captionRecordMile.setTextColor(getColor(R.color.colorAddCoverageStepTextViewInactive));

            captionStartCoverage.setEnabled(true);
            captionRecordCar.setEnabled(false);
            captionRecordMile.setEnabled(false);

        } else if (coverageCurrentStep.equals(CoverageCurrentStep.COVERAGE_ADDED)) {

            captionStartCoverage.setBackgroundResource(0);
            captionRecordCar.setBackgroundResource(R.drawable.coverage_add_button);
            captionRecordMile.setBackgroundResource(0);
            buttonGotit.setEnabled(false);
            buttonGotit.setBackgroundResource(R.drawable.inactive_button);

            buttonStartCoverage.setBackgroundResource(R.drawable.add_coverage_step_button_back_active);
            buttonStartCoverage.setEnabled(true);

            buttonRecordCar.setBackgroundResource(R.drawable.add_coverage_step_button_back_active);
            buttonRecordCar.setEnabled(true);

            buttonRecordMile.setBackgroundResource(R.drawable.add_coverage_step_button_back_inactive);
            buttonRecordMile.setEnabled(false);

            captionStartCoverage.setTextColor(getColor(R.color.colorNormalBlue));
            captionRecordCar.setTextColor(getColor(R.color.colorNormalBlue));
            captionRecordMile.setTextColor(getColor(R.color.colorAddCoverageStepTextViewInactive));

            captionStartCoverage.setEnabled(true);
            captionRecordCar.setEnabled(true);
            captionRecordMile.setEnabled(false);

        } else if (coverageCurrentStep.equals(CoverageCurrentStep.VIDEO_VEHICLE_ADDED)) {

            captionStartCoverage.setBackgroundResource(0);
            captionRecordCar.setBackgroundResource(0);
            captionRecordMile.setBackgroundResource(R.drawable.coverage_add_button);
            buttonGotit.setEnabled(false);
            buttonGotit.setBackgroundResource(R.drawable.inactive_button);

            buttonStartCoverage.setBackgroundResource(R.drawable.add_coverage_step_button_back_active);
            buttonStartCoverage.setEnabled(true);

            buttonRecordCar.setBackgroundResource(R.drawable.add_coverage_step_button_back_active);
            buttonRecordCar.setEnabled(true);

            buttonRecordMile.setBackgroundResource(R.drawable.add_coverage_step_button_back_active);
            buttonRecordMile.setEnabled(true);

            captionStartCoverage.setTextColor(getColor(R.color.colorNormalBlue));
            captionRecordCar.setTextColor(getColor(R.color.colorNormalBlue));
            captionRecordMile.setTextColor(getColor(R.color.colorNormalBlue));

            captionStartCoverage.setEnabled(true);
            captionRecordCar.setEnabled(true);
            captionRecordMile.setEnabled(true);

        } else {
            captionStartCoverage.setBackgroundResource(0);
            captionRecordCar.setBackgroundResource(0);
            captionRecordMile.setBackgroundResource(0);

            buttonGotit.setBackgroundResource(R.drawable.active_button);
            buttonGotit.setEnabled(true);

            buttonStartCoverage.setBackgroundResource(R.drawable.add_coverage_step_button_back_active);
            buttonStartCoverage.setEnabled(true);

            buttonRecordCar.setBackgroundResource(R.drawable.add_coverage_step_button_back_active);
            buttonRecordCar.setEnabled(true);

            buttonRecordMile.setBackgroundResource(R.drawable.add_coverage_step_button_back_active);
            buttonRecordMile.setEnabled(true);

            captionStartCoverage.setTextColor(getColor(R.color.colorNormalBlue));
            captionRecordCar.setTextColor(getColor(R.color.colorNormalBlue));
            captionRecordMile.setTextColor(getColor(R.color.colorNormalBlue));

            captionStartCoverage.setEnabled(true);
            captionRecordCar.setEnabled(true);
            captionRecordMile.setEnabled(true);

        }

        if (Globals.coverage == null) {

            layoutCoverage.setVisibility(View.GONE);
            layoutCoverageLineHidden.setVisibility(View.VISIBLE);

            layoutVideoVehicle.setVisibility(View.GONE);
            layoutVideoVehicleLineHiden.setVisibility(View.VISIBLE);

            layoutVideoMile.setVisibility(View.GONE);
            layoutVideoMileLineHidden.setVisibility(View.VISIBLE);
            return;
        }

        layoutCoverage.setVisibility(View.VISIBLE);
        layoutCoverageLineHidden.setVisibility(View.GONE);

        captionStartCoverage.setMinHeight(0);

        if (Globals.coverage.getLocationAddress() == null || Globals.coverage.getLocationAddress().isEmpty()) {
            layoutCoverage.setVisibility(View.GONE);
            layoutCoverageLineHidden.setVisibility(View.VISIBLE);
        } else {
            buttonLocation.setText(Globals.coverage.getLocationAddress());
        }

        if (Globals.coverage.getPeriod().isEmpty()) {
            layoutCoverage.setVisibility(View.GONE);
            layoutCoverageLineHidden.setVisibility(View.VISIBLE);
        } else {
            buttonPeriod.setText(Globals.coverage.getPeriod());
        }

        if (Globals.coverage.getCompany() == null) {

            layoutCoverage.setVisibility(View.GONE);
            layoutCoverageLineHidden.setVisibility(View.VISIBLE);

        } else {
            buttonCompany.setText(Globals.coverage.getCompany().getName());
        }

        if (Globals.coverage.getUrlImageVehicle() == null || Globals.coverage.getUrlImageVehicle().isEmpty()) {
            layoutVideoVehicleLineHiden.setVisibility(View.VISIBLE);
            layoutVideoVehicle.setVisibility(View.GONE);
        } else {
            layoutVideoVehicleLineHiden.setVisibility(View.GONE);
            layoutVideoVehicle.setVisibility(View.VISIBLE);

            Picasso picasso = Picasso.get();
            picasso.invalidate(Globals.coverage.getUrlImageVehicle());
            picasso.load(Globals.coverage.getUrlImageVehicle()).placeholder(R.drawable.history_row_item_image_corner_radius).into(imageViewVideoVehicle);
        }


        if (Globals.coverage.getUrlImageMile() == null || Globals.coverage.getUrlImageMile().isEmpty()) {

            layoutVideoMileLineHidden.setVisibility(View.VISIBLE);
            layoutVideoMile.setVisibility(View.GONE);
        } else {
            layoutVideoMileLineHidden.setVisibility(View.GONE);
            layoutVideoMile.setVisibility(View.VISIBLE);

            Picasso picasso = Picasso.get();
            picasso.invalidate(Globals.coverage.getUrlImageMile());
            picasso.load(Globals.coverage.getUrlImageMile()).placeholder(R.drawable.history_row_item_image_corner_radius).into(imageViewVideoMile);
        }


        isEditMode = false;
        if (isEditMode) {
            buttonEdit.setVisibility(View.GONE);
            imageButtonDeleteCoverage.setVisibility(View.VISIBLE);
            imageButtonDeleteVideoVehicle.setVisibility(View.VISIBLE);
            imageButtonDeleteVideoMile.setVisibility(View.VISIBLE);

        } else {
            buttonEdit.setVisibility(View.GONE);
            imageButtonDeleteCoverage.setVisibility(View.GONE);
            imageButtonDeleteVideoVehicle.setVisibility(View.GONE);
            imageButtonDeleteVideoMile.setVisibility(View.GONE);
        }


    }

    /**
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coverage);

        getControlHandlersAndLinkActions();

        initVariables();

        updateView();


    }

    private void initVariables() {

        if (Globals.coverage == null) {
            coverageCurrentStep = CoverageCurrentStep.NEW;
            isEditMode = false;
        } else {
            if (Globals.coverage.getState() == null) {
                isEditMode = false;
            } else {
                if (Globals.coverage.getState() == CoverageState.UNCOVERED) {
                    isEditMode = true;
                } else {
                    isEditMode = false;
                }
            }

            if (Globals.coverage.getId() == null) {
                coverageCurrentStep = CoverageCurrentStep.NEW;
            } else if (Globals.coverage.getId() < 1) {
                coverageCurrentStep = CoverageCurrentStep.NEW;
            } else {

                if (Globals.coverage.getTitle() == null) {

                    coverageCurrentStep = CoverageCurrentStep.NEW;
                } else {
                    coverageCurrentStep = CoverageCurrentStep.COVERAGE_ADDED;
                }

                if (Globals.coverage.getUrlVideoVehicle() != null && !Globals.coverage.getUrlVideoVehicle().isEmpty()) {
                    coverageCurrentStep = CoverageCurrentStep.VIDEO_VEHICLE_ADDED;
                }
                if (Globals.coverage.getUrlVideoMile() != null && !Globals.coverage.getUrlVideoMile().isEmpty()) {
                    coverageCurrentStep = CoverageCurrentStep.COMPLETED;
                }
            }
        }
    }

    /**
     * OnClick Handlers
     *
     * @param view
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.caption_start_coverage:
            case R.id.imageview_start_coverage:

                navigateToStartCoveragePage();
                break;

            case R.id.caption_record_car:
            case R.id.button_record_car:

                navigateToRecordVehicleActivity();
                break;

            case R.id.caption_record_mile:
            case R.id.button_record_mile:

                navigateToRecordMileActivity();
                break;

            case R.id.button_back_to_onboarding:
                finish();
                break;

            case R.id.button_got_it:
                setCoverageStateAsCovered();
                break;

            case R.id.button_delete_coverage:

                break;
            case R.id.button_delete_miles:
                break;
            case R.id.button_delete_video:
                break;
        }
    }

    ProgressDialog progressDialog;
    
    private void showWaitingScreen() {

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
        }

        try {
            progressDialog.show();
        } catch (Exception e) {
            //Utils.appendLog(System.err.toString());
            e.printStackTrace();
        }

    }

    private void hideWaitingScreen() {


        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            //Utils.appendLog(System.err.toString());
            e.printStackTrace();
        }


    }
    private void setCoverageStateAsCovered() {

        showWaitingScreen();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(
                Request.Method.POST, Constants.SERVER_HTTP_URL + "/api/add-coverage",
                new com.android.volley.Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideWaitingScreen();

                        String res = new String(response.data);
                        try {
                            JSONObject jsonObject = new JSONObject(res);
                            JSONObject data = jsonObject.getJSONObject("data");

                            if (jsonObject.getString("success").equals("true")) {

                                Constants.needHistoryRefresh = true;

                                Toast.makeText(AddCoverageActivity.this, data.getString("message"), Toast.LENGTH_SHORT).show();
                                Globals.coverage.setId(data.getLong("coverage_id"));
                                setResult(RESULT_OK);

                                finish();

                            } else {
                                Toast.makeText(AddCoverageActivity.this, data.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            //Utils.appendLog(System.err.toString());
                            e.printStackTrace();
                            Toast.makeText(AddCoverageActivity.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideWaitingScreen();
                Toast.makeText(AddCoverageActivity.this, getResources().getString(R.string.message_no_response), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("access_token", SharedHelper.getKey(AddCoverageActivity.this, "access_token"));
                params.put("state", String.format("%d", CoverageState.COVERED.getIntValue()));

                return params;
            }

            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() throws AuthFailureError {
                Map<String, VolleyMultipartRequest.DataPart> params = new HashMap<>();


                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.CONNECTION_TIMEOUT * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(volleyMultipartRequest);
    }

    /**
     * navigate to add new coverage activity
     */
    private void navigateToStartCoveragePage() {

        Intent intent = new Intent(AddCoverageActivity.this, StartCoverageActivity.class);
        startActivityForResult(intent, START_COVERAGE_ACTIVITY_REQUEST);
    }

    /**
     * navigate to record vehicle activity
     */
    private void navigateToRecordVehicleActivity() {

        Intent intent = new Intent(AddCoverageActivity.this, RecordVehicleActivity.class);
        startActivityForResult(intent, RECORD_VEHICLE_ACTIVITY_REQUEST);
    }

    /**
     * navigate to record vehicle activity
     */
    private void navigateToRecordMileActivity() {

        Intent intent = new Intent(AddCoverageActivity.this, RecordMileActivity.class);
        startActivityForResult(intent, RECORD_MILE_ACTIVITY_REQUEST);
    }

    private void completeAddCoverage() {

        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == START_COVERAGE_ACTIVITY_REQUEST) {

            if (resultCode == RESULT_OK) {

                coverageCurrentStep = CoverageCurrentStep.COVERAGE_ADDED;
                updateView();
            }
        } else if (requestCode == RECORD_VEHICLE_ACTIVITY_REQUEST) {

            if (resultCode == RESULT_OK) {

                coverageCurrentStep = CoverageCurrentStep.VIDEO_VEHICLE_ADDED;
                updateView();
            }
        } else if (requestCode == RECORD_MILE_ACTIVITY_REQUEST) {

            if (resultCode == RESULT_OK) {

                coverageCurrentStep = CoverageCurrentStep.COMPLETED;
                updateView();
            }
        }
    }

    @Override
    public void onBackPressed() {

        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
