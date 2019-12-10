package com.drizzle.carrental.globals;

import android.app.Application;

import com.drizzle.carrental.enumerators.ServiceArea;
import com.drizzle.carrental.models.MyProfile;
import com.drizzle.carrental.models.VehicleType;

import lombok.Getter;
import lombok.Setter;

public class Globals extends Application {


    @Getter
    @Setter
    private boolean isLoggedIn = false;

    @Getter
    @Setter
    private String APIToken = "";

    @Getter
    @Setter
    private MyProfile profile = new MyProfile();

    public static VehicleType  selectedVehicleType = new VehicleType();
    public static ServiceArea selectedServiceArea = new ServiceArea();


}
