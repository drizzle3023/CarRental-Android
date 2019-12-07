package com.drizzle.carrental.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.R;
import com.drizzle.carrental.activities.HomeActivity;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;

public class LoginFragment extends Fragment {

    private Button loginButton;
    private Country selectedCountry = null;
    private TextView countryNumber;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        loginButton = (Button) view.findViewById(R.id.login_button);
        countryNumber = (TextView) view.findViewById(R.id.text_country_prefix);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((Globals) getActivity().getApplication()).setLoggedIn(true);

                Intent intent = new Intent(getContext(), HomeActivity.class);

                startActivity(intent);

            }
        });

        countryNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CountryPicker.Builder builder = new CountryPicker.Builder().with(getActivity()).listener(new OnCountryPickerListener() {
                    @Override
                    public void onSelectCountry(Country country) {
                        selectedCountry = country;

                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                        if (imm.isAcceptingText()) {
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

}
