package com.drizzle.carrental.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriptionModel {

    double pricePerYear; // 47.99 €

    VehicleType carType; //car or ...

    int locationArea; // US or Europe

}
