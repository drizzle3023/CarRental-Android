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
import com.drizzle.carrental.activities.AddCoverageActivity;
import com.drizzle.carrental.activities.ClaimsActivity;

public class CoverageFragmentFull extends Fragment implements View.OnClickListener {

    private ImageButton buttonStartCoverage;
    private Button buttonClaims;


    private void getControlHandlersAndLinkActions(View view) {

        buttonStartCoverage = view.findViewById(R.id.button_start_coverage);
        buttonClaims = view.findViewById(R.id.button_claims);

        buttonStartCoverage.setOnClickListener(this);
        buttonClaims.setOnClickListener(this);

    }

    private void updateView() {

    }

    private void initVariables() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_coverage_full, container, false);

        getControlHandlersAndLinkActions(view);

        initVariables();

        updateView();

        return view;


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.button_start_coverage:
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
        startActivity(intent);
    }

    /**
     * navigateToClaimsActivity
     */
    private void navigateToClaimsActivity() {
        Intent intent = new Intent(getActivity(), ClaimsActivity.class);
        startActivity(intent);
    }
}

