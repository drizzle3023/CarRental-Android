package com.drizzle.carrental.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.drizzle.carrental.R;
import com.drizzle.carrental.activities.VerifyCodeActivity;
import com.drizzle.carrental.globals.Constants;
import com.drizzle.carrental.globals.Utils;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;

//import com.mukesh.countrypicker.Country;
//import com.mukesh.countrypicker.CountryPicker;

//import butterknife.BindView;

public class SignupFragment extends Fragment implements View.OnClickListener {

    //private Country selectedCountry = null;

//    @BindView(R.id.text_country_prefix)
//    TextView countryPrefixTextView;

    private Country selectedCountry = null;

    boolean ifAgreeTerm = false;
    private Button buttonCreateAccount;
    private CheckBox checkAgreeTerm;
    private EditText editTextCountryNumber;
    private EditText editTextPhoneNumber;
    private EditText editTextEmailAddress;

    private void getControlHandlersAndLinkActions(View view) {

        buttonCreateAccount = view.findViewById(R.id.createaccount_button);
        checkAgreeTerm = view.findViewById(R.id.agree_term_checkbox);
        editTextCountryNumber = view.findViewById(R.id.text_country_prefix);
        editTextPhoneNumber = view.findViewById(R.id.edittext_phonenumber);
        editTextEmailAddress = view.findViewById(R.id.edittext_email_address);

        int code = Utils.getCurrentCountryCode(getActivity());
        String countryCode = "+" + code;
        editTextCountryNumber.setText(countryCode);

        buttonCreateAccount.setOnClickListener(this);
        checkAgreeTerm.setOnClickListener(this);
        editTextCountryNumber.setOnClickListener(this);
        editTextEmailAddress.setOnClickListener(this);
    }


    private void updateView() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        getControlHandlersAndLinkActions(view);

        updateView();

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

    private void showAlert(String title, String message, int resourceId) {


        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

        if (resourceId == R.id.edittext_email_address) {

            editTextEmailAddress.requestFocus();
        } else if (resourceId == R.id.edittext_phonenumber) {

            editTextPhoneNumber.requestFocus();
        }
    }

    private void submitSignUpRequestAndNavigateToVerifyScreen() {

        String strEmail = editTextEmailAddress.getText().toString();
        String strPhone = editTextPhoneNumber.getText().toString();

//        if (!Utils.isValidMail(strEmail)) {
//
//            showAlert(getString(R.string.default_message_title), getString(R.string.validation_wrong_email), R.id.edittext_email_address);
//        } else if (!Utils.isValidMobile(strPhone)) {
//
//            showAlert(getString(R.string.default_message_title), getString(R.string.validation_wrong_phonenumber), R.id.edittext_phonenumber);
//        } else {

            Intent intent = new Intent(getActivity(), VerifyCodeActivity.class);
            getActivity().startActivity(intent);
//        }

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.createaccount_button) {
            if (ifAgreeTerm) {

                submitSignUpRequestAndNavigateToVerifyScreen();
            } else {
                return;
            }

        } else if (view.getId() == R.id.agree_term_checkbox) {
            if (checkAgreeTerm.isChecked()) {
                ifAgreeTerm = true;
            } else {
                ifAgreeTerm = false;
            }
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
}
