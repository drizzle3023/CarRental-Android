package com.drizzle.carrental.globals;

import android.location.Location;

public class Constants {

    public static final String SHARED_PREFERENCES_NAME = "com.drizzle.carrental";

    public static final String SHARED_PREFERENCE_KEY_API_TOKEN = "api_token";

    public static final String DATE_FORMAT = "d MMM";

    public static final String DATE_FORMAT_FOR_CLAIM = "EEEE, MMMM dd, yyyy";

    public static final String DATE_FORMAT_GENERAL = "MMMM dd, yyyy";

    public static final float DEFAULT_MAP_ZOOM_LEVEL = 14;

    public static final  String VEHICLE_VIDEO_FILE_NAME = "CarRentalVehicle.mp4";

    public static final  String MILE_VIDEO_FILE_NAME = "CarRentalMile.mp4";

    public static final  String DAMAGED_VIDEO_FILE_NAME = "DamagedPart.mp4";

    public static final  String VEHICLE_IMAGE_FILE_NAME = "CarRentalVehicle.png";

    public static final  String MILE_IMAGE_FILE_NAME = "CarRentalMile.png";

    public static final String SERVER_HTTP_URL = "http://192.168.1.11";

    //public static final String SERVER_HTTP_URL = "https://sandbox-zone02.netherlands.region.habit.io";

    public static final String ADYEN_PAYMENT_PUBLIC_KEY = "10001|D9DA89CB4E77D6BFDF96484EBB678A4ED1B8B0623690044EDA274E1F2BE64470CE29759F95ED0AD15016E71FDF4DD9A2EA044BACC190AC19B8373123EBF3FEF6C8B98DF83FE828D187A767FB8D32913820F97629FA38628917DFDCB50CB8B7865238B68A54361611EA7A179B4002E2EE27D17E274267E5F89A4FDC00F4F2E60039B8D35EB374EE6F1ECA74E5CFDEF82BD85E776CC7381F0ED27FF2EC92293AA738C8C6D20C8C0C49F961ADAABA3FDE9BCB3FB012666CE3C78B33CC0E2F1D34AA90BF984F0D42B30E498A86A7FC11B9407F57EBFDC113F2DA96EA294EFE14C37472D2D0D1BC1886D7424470DF9B9F232B67AA4DA3065DD723AE93524D82718E39";


    public static final String HISTORY_TYPE_COVERAGE = "Coverage";

    public static final String HISTORY_TYPE_PAYMENT = "Payment";

    public static final String CURRENCY_EURO = "EUR";

    public static final String CURRENCY_USD = "USD";
    /**
     * Temporary variables
     */

    public static boolean isNavigateToSignupOrLogin = true; //true - signup, false - login

    public static int isRecordingVehicleOrMileOrDamagedPart = 1; //true - recording vehicle, false - recording mile


    public static Location selectedLocation = new Location("selectedLocation"); //selected location when adding a new claim




}
