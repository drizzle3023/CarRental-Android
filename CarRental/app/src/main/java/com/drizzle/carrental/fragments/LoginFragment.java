package com.drizzle.carrental.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.drizzle.carrental.activities.VerifyCodeActivity;
import com.drizzle.carrental.api.ApiClient;
import com.drizzle.carrental.api.ApiInterface;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.R;
import com.drizzle.carrental.activities.HomeActivity;
import com.drizzle.carrental.globals.Utils;
import com.drizzle.carrental.serializers.ParseHistory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private Button loginButton;
    private Country selectedCountry = null;
    private TextView countryNumber;
    private EditText phoneNumber;
    private String strPhoneNumber;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        loginButton = (Button) view.findViewById(R.id.login_button);
        countryNumber = (TextView) view.findViewById(R.id.text_country_prefix);
        phoneNumber = view.findViewById(R.id.login_edit_phone_number);

        int code = Utils.getCurrentCountryCode(getActivity());
        String countryCode = "+" + code;
        countryNumber.setText(countryCode);
        countryNumber.requestFocus();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                strPhoneNumber = phoneNumber.getText().toString();
                if (strPhoneNumber.equalsIgnoreCase("")){
                    Toast.makeText(getContext(), getString(R.string.required_phone_number), Toast.LENGTH_SHORT).show();
                } else{

                    strPhoneNumber = countryNumber.getText().toString() + strPhoneNumber;

                    // Send sign up request to server
                    final ProgressDialog progressDialog = new ProgressDialog(getContext());
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                    JsonObject gsonObject = new JsonObject();
                    try {
                        JSONObject paramObject = new JSONObject();

                        paramObject.put("mobile", strPhoneNumber);

                        JsonParser jsonParser = new JsonParser();
                        gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

                        apiInterface.signIn(gsonObject).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                progressDialog.dismiss();

                                try {
                                    JSONObject object = new JSONObject(response.body().string());

                                    if (object.getString("success").equals("true")){

                                        Intent newIntent = new Intent(getActivity(), VerifyCodeActivity.class);
                                        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(newIntent);


                                    } else{
                                        JSONObject data = object.getJSONObject("data");
                                        Toast.makeText(getContext(), data.getString("message"), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(getContext(), "Server connect error", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                progressDialog.dismiss();
                                t.printStackTrace();
                                Toast.makeText(getContext(), "Server connect error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e){
                        progressDialog.dismiss();
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Server connect error", Toast.LENGTH_SHORT).show();
                    }
                }
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
