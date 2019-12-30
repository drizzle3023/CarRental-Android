package com.drizzle.carrental.globals;


import com.drizzle.carrental.enumerators.ServiceArea;
import com.drizzle.carrental.models.Coverage;
import com.drizzle.carrental.models.MyProfile;
import com.drizzle.carrental.models.VehicleType;

import lombok.Getter;
import lombok.Setter;

public class Globals {

    public static boolean isLoggedIn = false;

    public static String AccessToken = "";

    public static MyProfile profile = new MyProfile();

    public static VehicleType  selectedVehicleType = new VehicleType();
    public static ServiceArea selectedServiceArea = new ServiceArea();

    public static int paymentId = 0;

    public static Coverage coverage = new Coverage(); //current active coverage

}
