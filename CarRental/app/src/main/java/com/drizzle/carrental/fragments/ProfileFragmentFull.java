package com.drizzle.carrental.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.drizzle.carrental.R;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.models.MyProfile;

public class ProfileFragmentFull extends Fragment implements View.OnClickListener {


    MyProfile profile;

    TextView textViewName;
    TextView textViewPhoneNumber;
    TextView textViewCardNo;

    TextView linkAddEmail;
    TextView linkFeedback;
    TextView linkFaqs;
    TextView linkPermissions;
    TextView linkAbout;
    TextView linkLogout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_full, container, false);

        profile = Globals.profile;

        textViewName = (TextView) view.findViewById(R.id.textview_name);
        textViewPhoneNumber = (TextView) view.findViewById(R.id.textview_phonenumber);
        textViewCardNo = (TextView) view.findViewById(R.id.link_card_no);
        linkAddEmail = (TextView) view.findViewById(R.id.link_add_email);
        linkFeedback = (TextView) view.findViewById(R.id.link_feedback);
        linkFaqs = (TextView) view.findViewById(R.id.link_faqs);
        linkPermissions = (TextView) view.findViewById(R.id.link_permissions);
        linkAbout = (TextView) view.findViewById(R.id.link_about);
        linkLogout = (TextView) view.findViewById(R.id.link_logout);


        textViewCardNo.setOnClickListener(this);
        linkAddEmail.setOnClickListener(this);
        linkFeedback.setOnClickListener(this);
        linkFaqs.setOnClickListener(this);
        linkPermissions.setOnClickListener(this);
        linkAbout.setOnClickListener(this);
        linkLogout.setOnClickListener(this);

        textViewName.setText(profile.getName());
        textViewPhoneNumber.setText(profile.getMobile());

        try {
            int cardNoLength = profile.getCreditCardNo().length();
            textViewCardNo.setText("****" + profile.getCreditCardNo().substring(cardNoLength - 4));
        }
        catch (Exception e) {
            textViewCardNo.setText("****");
        }



        return view;

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.link_card_no) {

        }

        if (view.getId() == R.id.link_add_email) {


        }
        if (view.getId() == R.id.link_feedback) {


        }
        if (view.getId() == R.id.link_faqs) {


        }
        if (view.getId() == R.id.link_permissions) {


        }
        if (view.getId() == R.id.link_about) {


        }
        if (view.getId() == R.id.link_logout) {

        }

    }
}
