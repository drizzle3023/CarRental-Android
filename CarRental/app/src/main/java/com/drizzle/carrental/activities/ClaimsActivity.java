package com.drizzle.carrental.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.drizzle.carrental.R;
import com.drizzle.carrental.adapters.CustomAdapterForClaimListView;
import com.drizzle.carrental.adapters.CustomAdapterForHistoryListView;
import com.drizzle.carrental.api.ApiClient;
import com.drizzle.carrental.api.ApiInterface;
import com.drizzle.carrental.enumerators.ClaimState;
import com.drizzle.carrental.globals.Constants;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.globals.SharedHelper;
import com.drizzle.carrental.models.Claim;
import com.drizzle.carrental.models.VehicleType;
import com.drizzle.carrental.serializers.ParseClaim;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClaimsActivity extends Activity implements View.OnClickListener, Callback<ResponseBody> {

    public static final int CLAIM_ADD_REQUEST = 1;
    /**
     * UI Control Handlers
     */
    private Button buttonFileAClaim;
    private ListView listView;
    private ImageButton buttonBack;

    private static CustomAdapterForClaimListView adapter;

    ProgressDialog progressDialog;

    ArrayList<Claim> dataModels;
    ArrayList<ParseClaim> parseDataModels;

    /**
     * get control handlers by id and add listeners
     */
    private void getControlHandlersAndLinkActions() {

        buttonFileAClaim = findViewById(R.id.button_file_a_claim);
        buttonBack = findViewById(R.id.button_back);

        listView = findViewById(R.id.list_claims);

        dataModels = new ArrayList<>();

        //prepareTestData();

        adapter = new CustomAdapterForClaimListView(dataModels, this);

        listView.setAdapter(adapter);

        buttonFileAClaim.setOnClickListener(this);
        buttonBack.setOnClickListener(this);


    }

    private void prepareTestData() {

        for (int i = 0; i < 10; i++) {

            Claim claim = new Claim();
            claim.setAddressHappened("Independence Distreet, 37 ");
            if (i % 4 == 0) {
                claim.setClaimState(ClaimState.APPROVED);
            } else if (i % 4 == 1) {
                claim.setClaimState(ClaimState.NOT_APPROVED);
            } else if (i % 4 == 2) {
                claim.setClaimState(ClaimState.INCOMPLETE);
            } else {
                claim.setClaimState(ClaimState.EXPERT_UNDERGOING);
            }
            claim.setWhenHappened(new GregorianCalendar());
            claim.setWhatHappened("Glass Damaged");

            dataModels.add(claim);
        }
    }

    /**
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claims);

        progressDialog = new ProgressDialog(this);

        getControlHandlersAndLinkActions();

        fetchClaimListFromServer();

    }


    /**
     * OnClick Handlers
     *
     * @param view
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.button_file_a_claim:
                Globals.selectedClaim = new Claim();
                navigateToFileAClaimActivity();
                break;
            case R.id.button_back:
                finish();
                break;
        }
    }

    private void navigateToFileAClaimActivity() {

        Intent intent = new Intent(ClaimsActivity.this, AddClaimActivity.class);
        startActivityForResult(intent, CLAIM_ADD_REQUEST);
    }

    private void fetchClaimListFromServer() {

        //prepare restrofit2 request parameters
        JsonObject gSonObject = new JsonObject();

        //set parameters using org.JSONObject
        JSONObject paramObject = new JSONObject();
        try {

            paramObject.put("access_token", SharedHelper.getKey(this, "access_token"));
            paramObject.put("coverage_id", Globals.coverage.getId());
        } catch (JSONException e) {

            e.printStackTrace();
        }

        JsonParser jsonParser = new JsonParser();
        gSonObject = (JsonObject) jsonParser.parse(paramObject.toString());

        //get apiInterface
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        //display waiting dialog
        showWaitingScreen();
        //send request
        apiInterface.getClaimList(gSonObject).enqueue(this);
    }

    private void showWaitingScreen() {

        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideWaitingScreen() {

        progressDialog.dismiss();
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        hideWaitingScreen();

        String responseString = null;
        try {
            ResponseBody body = response.body();
            if (body != null) {
                responseString = body.string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject object = null;
        if (responseString != null) {
            try {
                object = new JSONObject(responseString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {

            Toast.makeText(this, R.string.message_no_response, Toast.LENGTH_SHORT).show();
            return;
        }

        if (object == null) {

            Toast.makeText(this, R.string.message_no_response, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (object.getString("success").equals("true")) {

                JSONObject data = object.getJSONObject("data");
                JSONArray listObject = data.getJSONArray("claimList");

                dataModels.clear();
                parseDataModels = new Gson().fromJson(listObject.toString(), new TypeToken<List<ParseClaim>>() {}.getType());

                for (int i = 0; i < parseDataModels.size(); i ++) {

                    ParseClaim parseClaim = parseDataModels.get(i);
                    Claim claim = new Claim();

                    claim.setId(parseClaim.getId());
                    claim.setWhatHappened(parseClaim.getWhatHappened());

                    GregorianCalendar calendar = new GregorianCalendar();
                    calendar.setTimeInMillis(parseClaim.getTimeHappened() * 1000);

                    claim.setWhenHappened(calendar);

                    Location location = new Location("location");
                    location.setLatitude(parseClaim.getLatitude());
                    location.setLongitude(parseClaim.getLongitude());
                    claim.setWhereHappened(location);

                    claim.setAddressHappened(parseClaim.getAddress());

                    claim.setDamagedPartsFromString(parseClaim.getDamagedPart());
                    if (parseClaim.getVideo() != null && !parseClaim.getVideo().isEmpty()) {
                        claim.setVideoURL(Constants.MEDIA_PATH_URL + parseClaim.getVideo());
                    }
                    if (parseClaim.getImage() != null && !parseClaim.getImage().isEmpty()) {
                        claim.setImageURL(Constants.MEDIA_PATH_URL + parseClaim.getImage());
                    }

                    claim.setExtraDescription(parseClaim.getNote());

                    claim.setClaimState(ClaimState.values()[parseClaim.getState() - 1]);

                    dataModels.add(claim);
                }
                adapter = new CustomAdapterForClaimListView(dataModels, this);

                listView.setAdapter(adapter);
                

            } else if (object.getString("success").equals("false")) {

                JSONObject data = object.getJSONObject("data");
                Toast.makeText(this, data.getString("message"), Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, R.string.message_no_response, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {

            Toast.makeText(this, R.string.message_no_response, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        hideWaitingScreen();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CLAIM_ADD_REQUEST) {
            //if (resultCode == RESULT_OK) {
                fetchClaimListFromServer();
            //}
        }
    }
}
