package com.drizzle.carrental.globals;


import com.drizzle.carrental.enumerators.ServiceArea;
import com.drizzle.carrental.models.Coverage;
import com.drizzle.carrental.models.MyProfile;
import com.drizzle.carrental.models.VehicleType;

public class Globals {

    public static boolean isLoggedIn = false;

    @Getter
    @Setter
    private boolean isLoggedIn = false;

    @Getter
    @Setter
    private String APIToken = "";

    @Getter
    @Setter
    private MyProfile profile = new MyProfile();

    public static String APIToken = "";

    public static MyProfile profile = new MyProfile();


    public static VehicleType  selectedVehicleType = new VehicleType();
    public static ServiceArea selectedServiceArea = new ServiceArea();

    public static int paymentId = 0;

    public static Coverage coverage = new Coverage(); //current active coverage

}
