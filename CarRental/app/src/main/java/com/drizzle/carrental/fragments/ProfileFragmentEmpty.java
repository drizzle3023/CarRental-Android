package com.drizzle.carrental.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.drizzle.carrental.BuildConfig;
import com.drizzle.carrental.R;
import com.drizzle.carrental.activities.SignUpLoginActivity;
import com.drizzle.carrental.activities.SubscriptionNewActivity;
import com.drizzle.carrental.globals.Constants;

public class ProfileFragmentEmpty extends Fragment implements View.OnClickListener {

    private TextView textViewLogin;
    private TextView textViewSignup;
    private TextView textViewFAQs;
    private TextView textViewAbout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_empty, container, false);

        textViewLogin = (TextView) view.findViewById(R.id.link_login);
        textViewSignup = (TextView) view.findViewById(R.id.link_signup);
        textViewFAQs = (TextView) view.findViewById(R.id.link_faqs);
        textViewAbout = (TextView) view.findViewById(R.id.link_about);

        textViewLogin.setOnClickListener(this);
        textViewSignup.setOnClickListener(this);
//        textViewFAQs.setOnClickListener(this);
//        textViewAbout.setOnClickListener(this);

        textViewFAQs.setText(Html.fromHtml("<a href='" + Constants.CONTACT_URL + "'><i>FAQs</i></a>"));
        textViewAbout.setText(Html.fromHtml("<a href='" + Constants.CONTACT_URL + "'><i>About " + BuildConfig.VERSION_NAME + "</i></a>"));


        textViewFAQs.setClickable(true);
        textViewFAQs.setMovementMethod(LinkMovementMethod.getInstance());

        textViewAbout.setClickable(true);
        textViewAbout.setMovementMethod(LinkMovementMethod.getInstance());

        return view;

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.link_login:
                navigateToLoginActivity();
                break;
            case R.id.link_signup:
                navigateToSignupActivity();
                break;

            case R.id.link_faqs:
            case R.id.link_about:
            default:
                break;
        }
    }

    private void navigateToSignupActivity() {

        Constants.isNavigateToSignupOrLogin = true;
        Intent intent = new Intent(getActivity(), SignUpLoginActivity.class);
        startActivity(intent);
    }

    private void navigateToLoginActivity() {

        Constants.isNavigateToSignupOrLogin = false;
        Intent intent = new Intent(getActivity(), SignUpLoginActivity.class);
        startActivity(intent);
    }
}
