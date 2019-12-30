package com.drizzle.carrental.globals;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.io.File;
import java.util.regex.Pattern;

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
}
