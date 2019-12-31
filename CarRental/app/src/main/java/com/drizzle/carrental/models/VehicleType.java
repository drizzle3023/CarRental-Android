package com.drizzle.carrental.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleType implements Serializable {

    @SerializedName("id")
    long id;

    @SerializedName("name")
    String name;

    @SerializedName("icon_url")
    String iconURL;

    @SerializedName("price_per_year")
    double pricePerYear;

    @SerializedName("currency")
    String currency;

}
