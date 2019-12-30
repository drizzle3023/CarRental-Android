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

    @SerializedName("start_at")
    double startAt;

    @SerializedName("end_at")
    double endAt;

    @SerializedName("video_mile")
    String videoMile;

    @SerializedName("vidoe_vehicle")
    String videoVehicle;

    int state;
}
