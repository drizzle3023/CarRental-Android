package com.drizzle.carrental.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.drizzle.carrental.R;
import com.drizzle.carrental.activities.VerifyCodeActivity;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;

//import com.mukesh.countrypicker.Country;
//import com.mukesh.countrypicker.CountryPicker;

//import butterknife.BindView;

public class SignupFragment extends Fragment {

    //private Country selectedCountry = null;

//    @BindView(R.id.text_country_prefix)
//    TextView countryPrefixTextView;

    private Country selectedCountry = null;

    boolean ifAgreeTerm = false;
    private Button createAccountButton;
    private CheckBox agreeTermCheckbox;
    private TextView countryNumber;
    private TextView phoneNumber;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        createAccountButton = (Button) view.findViewById(R.id.createaccount_button);
        agreeTermCheckbox = (CheckBox) view.findViewById(R.id.agree_term_checkbox);
        countryNumber = (TextView)  view.findViewById(R.id.text_country_prefix);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                // do signup
                if (ifAgreeTerm) {
                    Intent intent = new Intent( getActivity(), VerifyCodeActivity.class);
                    getActivity().startActivity(intent);
                }
                else {
                    return;
                }


            }
        });

        agreeTermCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (agreeTermCheckbox.isChecked()) {
                    ifAgreeTerm = true;
                }
                else {
                    ifAgreeTerm = false;
                }
                updateSignupButtonStyle();
            }
        });


        countryNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                CountryPicker.Builder builder = new CountryPicker.Builder().with(getActivity()).listener (new OnCountryPickerListener() {
                    @Override
                    public void onSelectCountry(Country country) {
                        selectedCountry = country;

                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                        if (imm.isAcceptingText()){
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        }

                        updateCountryViewInfo();
                    }
                });
                CountryPicker picker = builder.build();
                picker.showDialog(getActivity());
            }
        });

        return view;
    }


    void updateCountryViewInfo() {
        if (this.selectedCountry == null) {
            return;
        }

        this.countryNumber.setText(this.selectedCountry.getDialCode());
    }

    public void updateSignupButtonStyle() {
        if (ifAgreeTerm) {
            createAccountButton.setBackgroundResource(R.drawable.active_button);
        }
        else {
            createAccountButton.setBackgroundResource(R.drawable.inactive_button);
        }
    }
}
