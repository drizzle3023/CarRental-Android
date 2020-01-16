package com.drizzle.carrental.fragments;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.drizzle.carrental.R;
import com.drizzle.carrental.customcomponents.VerticalTextView;

public class OnboardingSlideFragment2 extends Fragment {

    private VerticalTextView textViewCompanyName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_onboarding_slide2, container, false);

        textViewCompanyName = rootView.findViewById(R.id.textview_company_name);
        String sourceString = "<i>" + getResources().getString(R.string.powered_by)+ "</i> " + " " + getResources().getString(R.string.habit_analyatics);
        textViewCompanyName .setText(Html.fromHtml(sourceString));

        return rootView;
    }



}
