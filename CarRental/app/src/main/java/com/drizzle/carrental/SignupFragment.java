package com.drizzle.carrental;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;

import butterknife.BindView;

public class SignupFragment extends Fragment {

    private Country selectedCountry = null;

    @BindView(R.id.text_country_prefix)
    TextView countryPrefixTextView;

    boolean ifAgreeTerm = false;
    private Button createAccountButton;
    private CheckBox agreeTermCheckbox;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        createAccountButton = (Button) view.findViewById(R.id.createaccount_button);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //
            }
        });

        agreeTermCheckbox = (CheckBox) view.findViewById(R.id.agree_term_checkbox);
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

        return view;
    }

    public void updateSignupButtonStyle() {
        if (ifAgreeTerm) {
            createAccountButton.setBackgroundResource(R.drawable.active_createaccount);
        }
        else {
            createAccountButton.setBackgroundResource(R.drawable.inactive_createaccount);
        }
    }

//    @OnClick(R.id.text_country_prefix)
//    void onCountryPrefixTextViewClicked() {
//        CountryPicker.Builder builder = new CountryPicker.Builder().with(this)
//                .listener(country -> {
//                    this.selectedCountry = country;
//
//                    InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
//
//                    if (imm.isAcceptingText()){
//                        Utils.hideKeyboard(SignUpActivity.this);
//                    }
//
//                    updateCountryViewInfo();
//                });
//        CountryPicker picker = builder.build();
//        picker.showDialog(this);
//    }

    void updateCountryViewInfo() {
        if (this.selectedCountry == null) {
            return;
        }

        this.countryPrefixTextView.setText("");

        this.countryPrefixTextView.setText(this.selectedCountry.getDialCode());


    }
}
