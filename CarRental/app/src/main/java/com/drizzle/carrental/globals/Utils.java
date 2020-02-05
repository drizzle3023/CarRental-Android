package com.drizzle.carrental.globals;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.drizzle.carrental.activities.OnboardingActivity;
import com.drizzle.carrental.enumerators.ServiceArea;
import com.drizzle.carrental.models.Coverage;
import com.drizzle.carrental.models.MyProfile;
import com.drizzle.carrental.models.VehicleType;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import io.habit.analytics.HabitStatusCodes;
import io.habit.analytics.SDK;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class Utils {

    public static boolean isValidMail(String email) {

        String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        return Pattern.compile(EMAIL_STRING).matcher(email).matches();
    }

    public static boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    public static int getCurrentCountryCode(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String countryIso = telephonyManager.getSimCountryIso().toUpperCase();
        return PhoneNumberUtil.getInstance().getCountryCodeForRegion(countryIso);
    }

    public static void showToast(Context context, String message) {

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static boolean removeTemporaryFile(String filePath) {

        File fDelete = new File(filePath);
        if (fDelete.exists()) {
            if (fDelete.delete()) {

                return true;
            } else {

                return false;
            }
        }
        else {
            return true;
        }

    }

    public static String getAddressFromLocation(Context context, Location location) {

        Geocoder geocoder;
        List<Address> addresses = new ArrayList<>();
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (Exception e) {
            e.printStackTrace();
        }

        String address = "";
        try {
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return address;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void resetAllGlobals() {

        Globals.isLoggedIn  = false;
        Globals.profile  = new MyProfile();
        Globals.selectedVehicleType  = new VehicleType();
        Globals.selectedServiceArea  = new ServiceArea();
        Globals.paymentId  = 0;
        Globals.coverage  = new Coverage(); //current active coverage
        Globals.mobileNumber = ""; //string phone number
        Globals.isSignUpOrLoginRequest  = true;
    }

    public static void initHabitSDK(Context context) {

//        SDK.INSTANCE.init(context, "", new Function1<HabitStatusCodes, Unit>() {
//
//            @Override
//            public Unit invoke(HabitStatusCodes habitStatusCodes) {
//                if (habitStatusCodes == HabitStatusCodes.HABIT_SDK_SET_AUTHENTICATION) {
//
//                    Constants.isHabitSDKReady = true;
//                    SDK.INSTANCE.setAuthorization(SharedHelper.getKey(context, "payload"));
//                }
//
//                return Unit.INSTANCE;
//            }
//        });


        //init habit analyatics sdk
        SDK.INSTANCE.init(context, "", SharedHelper.getKey(context, "payload"), new Function1<HabitStatusCodes, Unit>() {
            @Override
            public Unit invoke(HabitStatusCodes habitStatusCodes) {

                if (habitStatusCodes == HabitStatusCodes.HABIT_SDK_INITIALIZATION_SUCCESS) {
                    Constants.isHabitSDKReady = true;
                }
                else {
                    Constants.isHabitSDKReady = false;
                }
                Log.d("tiny-debug", "invoke: " + habitStatusCodes);

                return Unit.INSTANCE;
            }
        });

    }

    public static void setAuthHabitSDK(Context context) {

        if (Constants.isHabitSDKReady) {

            SDK.INSTANCE.setAuthorization(SharedHelper.getKey(context, "payload"));
        }
    }

    public static void logout(Context context, Activity activity) {

        if (Constants.isHabitSDKReady) {
            SDK.INSTANCE.logout();
        }

        SharedHelper.clearSharedPreferences(context);
        Utils.resetAllGlobals();
        Intent intent = new Intent(activity, OnboardingActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public static String encodeAsUTF8(String str) {

        try {
            return URLEncoder.encode(str, "UTF-8").replace("+", "%20");
        } catch (Exception e) {
            e.printStackTrace();
            return "";

        }
    }

}
