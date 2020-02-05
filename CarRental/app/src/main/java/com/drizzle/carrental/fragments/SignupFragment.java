package com.drizzle.carrental.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.drizzle.carrental.R;
import com.drizzle.carrental.activities.VerifyCodeActivity;
import com.drizzle.carrental.api.ApiClient;
import com.drizzle.carrental.api.ApiInterface;
import com.drizzle.carrental.globals.Constants;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.globals.Utils;
import com.drizzle.carrental.models.MyProfile;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.mukesh.countrypicker.Country;
//import com.mukesh.countrypicker.CountryPicker;

//import butterknife.BindView;

public class SignupFragment extends Fragment implements View.OnClickListener, Callback<ResponseBody> {

//private Country selectedCountry = null;

//    @BindView(R.id.text_country_prefix)
//    TextView countryPrefixTextView;

    ProgressDialog progressDialog;

    private Country selectedCountry = null;

    boolean ifAgreeTerm = false;
    private Button buttonCreateAccount;
    private CheckBox checkAgreeTerm;
    private EditText editTextCountryNumber;
    private EditText editTextPhoneNumber;
    private EditText editTextEmailAddress;
    private EditText editTextSignUpName;

    private Context context;

    private void getControlHandlersAndLinkActions(View view) {

        buttonCreateAccount = view.findViewById(R.id.createaccount_button);
        checkAgreeTerm = view.findViewById(R.id.agree_term_checkbox);
        editTextCountryNumber = view.findViewById(R.id.text_country_prefix);
        editTextPhoneNumber = view.findViewById(R.id.edittext_phonenumber);
        editTextEmailAddress = view.findViewById(R.id.edittext_email_address);
        editTextSignUpName = view.findViewById(R.id.edittext_signup_name);

        int code = Utils.getCurrentCountryCode(getActivity());
        String countryCode = "+" + code;
        editTextCountryNumber.setText(countryCode);

        buttonCreateAccount.setOnClickListener(this);
        checkAgreeTerm.setOnClickListener(this);
        editTextCountryNumber.setOnClickListener(this);
        editTextEmailAddress.setOnClickListener(this);
    }


    private void updateView() {


        checkAgreeTerm.setText(Html.fromHtml("I agree to the " +
                "<a href='" + Constants.CONTACT_URL + "'><i>Terms of Service</i></a>"));

        checkAgreeTerm.setClickable(true);
        checkAgreeTerm.setMovementMethod(LinkMovementMethod.getInstance());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        getControlHandlersAndLinkActions(view);

        progressDialog = new ProgressDialog(getActivity());

        updateView();

        context = getContext();


        return view;
    }


    void updateCountryViewInfo() {
        if (this.selectedCountry == null) {
            return;
        }

        this.editTextCountryNumber.setText(this.selectedCountry.getDialCode());
    }

    public void updateSignupButtonStyle() {
        if (ifAgreeTerm) {
            buttonCreateAccount.setBackgroundResource(R.drawable.active_button);
        } else {
            buttonCreateAccount.setBackgroundResource(R.drawable.inactive_button);
        }
    }

    private void showToast(String message, int resourceId) {

        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

        if (resourceId == R.id.edittext_email_address) {

            editTextEmailAddress.requestFocus();
        } else if (resourceId == R.id.edittext_phonenumber) {

            editTextPhoneNumber.requestFocus();
        } else if (resourceId == R.id.edittext_signup_name) {

            editTextSignUpName.requestFocus();
        }
    }

    private void submitSignUpRequest() {

        String strEmail = editTextEmailAddress.getText().toString();

        String strPhone = editTextCountryNumber.getText() + editTextPhoneNumber.getText().toString();
        String strName = editTextSignUpName.getText().toString();

        strPhone = strPhone.trim();
        strName = strName.trim();
        strEmail = strEmail.trim();

        if (strName.isEmpty()) {

            showToast(getString(R.string.validation_user_name_empty), R.id.edittext_signup_name);
            return;
        }

        if (strEmail.isEmpty()) {

            showToast(getString(R.string.validation_email_empty), R.id.edittext_email_address);
            return;
        }

        if (editTextPhoneNumber.getText().toString().isEmpty()) {

            showToast(getString(R.string.validation_mobile_empty), R.id.edittext_phonenumber);
            return;
        }

        strEmail = strEmail.replace(" ", "");

        if (!Utils.isValidMail(strEmail)) {

            showToast(getString(R.string.validation_wrong_email), R.id.edittext_email_address);
        } else if (!Utils.isValidMobile(strPhone)) {

            showToast(getString(R.string.validation_wrong_phonenumber), R.id.edittext_phonenumber);
        } else {

            // Send sign up request to server

            JSONObject paramObject = new JSONObject();

            try {

                paramObject.put("name", strName);
                paramObject.put("email", strEmail);
                paramObject.put("mobile", strPhone);

                if (Globals.selectedVehicleType != null) {
                    paramObject.put("car_type_id", Globals.selectedVehicleType.getId());
                }
                if (Globals.selectedServiceArea != null) {

                    if (Globals.selectedServiceArea.getAreaName().equals(getString(R.string.worldzone_europe))) {
                        paramObject.put("world_zone", getString(R.string.worldzone_europe_ab));
                    }
                    else if (Globals.selectedServiceArea.getAreaName().equals(getString(R.string.worldzone_us))){
                        paramObject.put("world_zone", getString(R.string.worldzone_us_ab));
                    }
                }
            } catch (Exception e) {

                e.printStackTrace();
                //Toast.makeText(getActivity(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }

            JsonParser jsonParser = new JsonParser();
            JsonObject gSonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            //get apiInterface
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            //display waiting dialog
            showWaitingScreen();
            //send request
            apiInterface.signUp(gSonObject).enqueue(this);
        }

    }

    private void showWaitingScreen() {

        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideWaitingScreen() {

        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.createaccount_button) {
            if (ifAgreeTerm) {

                submitSignUpRequest();
            } else {

                return;
            }

        } else if (view.getId() == R.id.agree_term_checkbox) {

            ifAgreeTerm = checkAgreeTerm.isChecked();
            updateSignupButtonStyle();

        } else if (view.getId() == R.id.text_country_prefix) {

            displayCountryPicker();
        }

    }

    private void displayCountryPicker() {

        CountryPicker.Builder builder = new CountryPicker.Builder().with(getActivity()).listener(new OnCountryPickerListener() {
            @Override
            public void onSelectCountry(Country country) {
                selectedCountry = country;

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm.isAcceptingText()) {
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }

                updateCountryViewInfo();
                editTextPhoneNumber.requestFocus();
            }
        });
        CountryPicker picker = builder.build();
        picker.showDialog(getActivity());
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject object = null;
        if (responseString != null) {
            try {
                object = new JSONObject(responseString);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            Toast.makeText(getActivity(), R.string.message_no_response, Toast.LENGTH_SHORT).show();
            return;
        }

        if (object == null) {

            Toast.makeText(getActivity(), R.string.message_no_response, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (object.getString("success").equals("true")) {

                String strPhone = editTextCountryNumber.getText().toString();
                strPhone = strPhone + editTextPhoneNumber.getText();

                Globals.mobileNumber = strPhone.trim();
                Globals.userName = editTextSignUpName.getText().toString().trim();
                Globals.emailAddress = editTextEmailAddress.getText().toString().trim();



                Globals.isSignUpOrLoginRequest = true;

                navigateToVerifyScreen();

            } else if (object.getString("success").equals("false")) {

                JSONObject data = object.getJSONObject("data");
                Toast.makeText(getContext(), data.getString("message"), Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(getActivity(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

            Toast.makeText(getActivity(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {

        hideWaitingScreen();
        Toast.makeText(getActivity(), R.string.message_no_response, Toast.LENGTH_SHORT).show();
    }

    private void navigateToVerifyScreen() {

        Intent newIntent = new Intent(getContext(), VerifyCodeActivity.class);
        startActivity(newIntent);
    }


}