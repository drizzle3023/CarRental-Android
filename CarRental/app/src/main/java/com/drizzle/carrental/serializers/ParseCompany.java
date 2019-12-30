package com.drizzle.carrental.serializers;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParseCompany implements Serializable {

    int id;

    String name;

    double latitude;

    double longitude;

    String address;

    @SerializedName("icon_url")
    String iconUrl;

    @SerializedName("price_per_year")
    float pricePerYear;
}
