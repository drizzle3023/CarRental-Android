package com.drizzle.carrental;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CoverageEmptyFragment extends Fragment {

    private ImageButton buttonBack;
    private ImageButton buttonContact;
    private ImageButton buttonHelp;
    private Button buttonLearnMore;
    private ImageButton buttonHistory;
    private ImageButton buttonCoverage;
    private ImageButton buttonProfile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_coverage_empty, container, false);

        buttonBack = (ImageButton) view.findViewById(R.id.coverageBackbutton);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                getActivity().finish();
            }
        });

        buttonLearnMore = (Button) view.findViewById(R.id.coverageButtonLearnmore);
        buttonLearnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(getActivity(),SubscriptionNewActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

}