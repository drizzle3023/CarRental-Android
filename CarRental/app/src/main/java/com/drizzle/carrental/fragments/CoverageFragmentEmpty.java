package com.drizzle.carrental.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.drizzle.carrental.R;
import com.drizzle.carrental.activities.OnboardingActivity;
import com.drizzle.carrental.activities.SubscriptionNewActivity;

public class CoverageFragmentEmpty extends Fragment implements View.OnClickListener {

    private ImageButton buttonBackToOnboarding;
    private ImageButton buttonContact;
    private ImageButton buttonInformation;
    private Button buttonLearnMore;
    private ImageButton buttonHistory;
    private ImageButton buttonCoverage;
    private ImageButton buttonProfile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_coverage_empty, container, false);

        buttonBackToOnboarding = view.findViewById(R.id.button_back_to_onboarding);
        buttonBackToOnboarding.setOnClickListener(this);

        buttonLearnMore = view.findViewById(R.id.button_learn_more);
        buttonLearnMore.setOnClickListener(this);

        buttonInformation = view.findViewById(R.id.button_information);
        buttonInformation.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_back_to_onboarding:
            case R.id.button_information:
                navigateToOnBoardingActivity();
                break;
            case R.id.button_learn_more:

                navigateToSubscriptionNewActivity();
                break;

        }
    }

    private void navigateToSubscriptionNewActivity() {

        Intent intent = new Intent(getActivity(), SubscriptionNewActivity.class);
        startActivity(intent);
    }

    private void navigateToOnBoardingActivity() {

        Intent intent = new Intent(getActivity(), OnboardingActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}