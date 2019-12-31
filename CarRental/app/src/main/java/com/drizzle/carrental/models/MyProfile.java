package com.drizzle.carrental.models;

import com.google.gson.annotations.SerializedName;

import org.intellij.lang.annotations.JdkConstants;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.GregorianCalendar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyProfile implements Serializable {


    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("mobile")
    private String mobile;

    @SerializedName("address")
    private String address;

    @SerializedName("card_no")
    private String creditCardNo;

}
