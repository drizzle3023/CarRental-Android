package com.drizzle.carrental.serializers;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParseCoverage implements Serializable {

    int id;

    String name;

    double latitude;

    double longitude;

    String address;

    JSONObject company;

    int state;

    @SerializedName("claim_count")
    int claimCount;

    @SerializedName("start_at")
    double startAt;

    @SerializedName("end_at")
    double endAt;


    @SerializedName("video_vehicle")
    String videoVehicle;

    @SerializedName("image_vehicle")
    String imageVehicle;

    @SerializedName("video_mile")
    String videoMile;

    @SerializedName("image_mile")
    String imageMile;

    @SerializedName("time_left")
    long remainingTime;

    @SerializedName("cancel_date")
    double operationDate;
}
