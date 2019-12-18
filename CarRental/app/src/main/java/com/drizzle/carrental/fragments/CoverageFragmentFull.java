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

public class CoverageFragmentFull extends Fragment implements View.OnClickListener {

    private ImageButton buttonStartCoverage;

    private void getControlHandlersAndLinkActions(View view) {

        buttonStartCoverage = (ImageButton) view.findViewById(R.id.button_start_coverage);

        buttonStartCoverage.setOnClickListener(this);

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

        if (view.getId() == R.id.button_start_coverage) {
            Intent intent=new Intent(getActivity(), AddCoverageActivity.class);
            startActivity(intent);
        }

    }
}

