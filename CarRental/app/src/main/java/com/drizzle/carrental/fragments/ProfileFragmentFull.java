package com.drizzle.carrental.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.drizzle.carrental.BuildConfig;
import com.drizzle.carrental.R;
import com.drizzle.carrental.activities.OnboardingActivity;
import com.drizzle.carrental.api.ApiClient;
import com.drizzle.carrental.api.ApiInterface;
import com.drizzle.carrental.globals.Constants;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.globals.SharedHelper;
import com.drizzle.carrental.globals.Utils;
import com.drizzle.carrental.models.MyProfile;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.habit.analytics.SDK;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragmentFull extends Fragment implements View.OnClickListener, Callback<ResponseBody> {

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


        textViewName = view.findViewById(R.id.textview_name);
        textViewPhoneNumber = view.findViewById(R.id.textview_phonenumber);
        textViewCardNo = view.findViewById(R.id.link_card_no);
        linkAddEmail = view.findViewById(R.id.link_add_email);
        linkFeedback = view.findViewById(R.id.link_feedback);
        linkFaqs =  view.findViewById(R.id.link_faqs);
        linkPermissions = view.findViewById(R.id.link_permissions);
        linkAbout =  view.findViewById(R.id.link_about);
        linkLogout = view.findViewById(R.id.link_logout);


        linkFeedback.setText(Html.fromHtml("<a href='" + Constants.CONTACT_URL + "'><i>Feedback</i></a>"));
        linkFaqs.setText(Html.fromHtml("<a href='" + Constants.CONTACT_URL + "'><i>FAQs</i></a>"));
        linkAbout.setText(Html.fromHtml("<a href='" + Constants.CONTACT_URL + "'><i>About " + BuildConfig.VERSION_NAME + "</i></a>"));

        linkFeedback.setClickable(true);
        linkFeedback.setMovementMethod(LinkMovementMethod.getInstance());

        linkFaqs.setClickable(true);
        linkFaqs.setMovementMethod(LinkMovementMethod.getInstance());

        linkAbout.setClickable(true);
        linkAbout.setMovementMethod(LinkMovementMethod.getInstance());

        textViewCardNo.setOnClickListener(this);
        linkAddEmail.setOnClickListener(this);

        linkPermissions.setOnClickListener(this);
        //linkAbout.setOnClickListener(this);
        linkLogout.setOnClickListener(this);

        if (Globals.profile.getName() == null || Globals.profile.getName().isEmpty()) {
            fetchProfileFromServer();
        } else {
            updateFragment();
        }


        return view;

    }

    public void updateFragment() {

        if (Globals.profile != null) {

            if (Globals.profile.getName() == null || Globals.profile.getName().isEmpty()) {

            }
            else {
                textViewName.setText(Globals.profile.getName());
            }

            if (Globals.profile.getMobile() == null || Globals.profile.getMobile().isEmpty()) {

            }
            else {
                textViewPhoneNumber.setText(Globals.profile.getMobile());
            }

            if (Globals.profile.getEmail() == null || Globals.profile.getEmail().isEmpty()) {


            }
            else {

                linkAddEmail.setText(Globals.profile.getEmail());
                linkAddEmail.setTextColor(getResources().getColor(R.color.colorNormalText, null));
            }

            textViewCardNo.setText("****");
//            try {
//                int cardNoLength = Globals.profile.getCreditCardNo().length();
//                textViewCardNo.setText("****" + Globals.profile.getCreditCardNo().substring(cardNoLength - 4));
//            } catch (Exception e) {
//                textViewCardNo.setText("****");
//            }
        }
    }

    /**
     * fetch user profile from saved access_token
     */
    private void fetchProfileFromServer() {

        //prepare restrofit2 request parameters
        JsonObject gSonObject = new JsonObject();

        //set parameters using org.JSONObject
        JSONObject paramObject = new JSONObject();
        try {

            paramObject.put("access_token", SharedHelper.getKey(getActivity(), "access_token"));
        } catch (Exception e) {

            e.printStackTrace();
        }

        JsonParser jsonParser = new JsonParser();
        gSonObject = (JsonObject) jsonParser.parse(paramObject.toString());

        //get apiInterface
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        //send request
        apiInterface.getUserProfile(gSonObject).enqueue(this);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.link_card_no) {

        }

        if (view.getId() == R.id.link_add_email) {


        }
        if (view.getId() == R.id.link_permissions) {


        }


        if (view.getId() == R.id.link_logout) {

            new AlertDialog.Builder(getActivity())
                    .setTitle("Logout")
                    .setMessage("Do you really want to logout?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            Utils.logout(getActivity(), getActivity());
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();

        }

    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        String responseString = null;
        try {
            ResponseBody body = response.body();
            if (body != null) {
                responseString = body.string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject object = null;
        if (responseString != null) {
            try {
                object = new JSONObject(responseString);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            Toast.makeText(getActivity(), R.string.message_no_response, Toast.LENGTH_SHORT).show();
            return;
        }


        if (object == null) {

            Toast.makeText(getActivity(), R.string.message_no_response, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (object.getString("success").equals("true")) {


                JSONObject data = object.getJSONObject("data");
                JSONObject profileData = data.getJSONObject("profile");
                MyProfile myProfile = new Gson().fromJson(profileData.toString(), new TypeToken<MyProfile>() {
                }.getType());

                Globals.profile = myProfile;

                updateFragment();

            } else if (object.getString("success").equals("false")) {

                JSONObject data = object.getJSONObject("data");
                Toast.makeText(getActivity(), data.getString("message"), Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(getActivity(), R.string.message_no_response, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

            Toast.makeText(getActivity(), R.string.message_no_response, Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {

        Toast.makeText(getActivity(), R.string.message_no_response, Toast.LENGTH_SHORT).show();
    }
}
