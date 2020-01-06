package com.drizzle.carrental.serializers;

import android.location.Location;

import com.drizzle.carrental.enumerators.ClaimState;
import com.drizzle.carrental.enumerators.DamagedPart;
import com.drizzle.carrental.globals.Constants;
import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParseClaim {

    @SerializedName("id")
    long id;

    @SerializedName("name")
    String name;

    @SerializedName("user_id")
    long userId;

    @SerializedName("what_happenend")
    String whatHappened;

    @SerializedName("time_happened")
    long timeHappened;

    @SerializedName("latitude")
    double latitude;

    @SerializedName("longitude")
    double longitude;

    @SerializedName("address")
    String address;

    @SerializedName("damaged_part")
    String damagedPart;

    @SerializedName("video")
    String video;

    @SerializedName("note")
    String note;

    @SerializedName("state")
    int state;
}
