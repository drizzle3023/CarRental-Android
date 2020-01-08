package com.drizzle.carrental.models;

import android.location.Location;
import android.security.keystore.StrongBoxUnavailableException;

import com.drizzle.carrental.enumerators.CoverageState;
import com.drizzle.carrental.globals.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Coverage {

    Long id;

    String title;

    boolean activeState;

    CoverageState state;

    Company company;

    GregorianCalendar dateFrom;

    GregorianCalendar dateTo;

    Location location;

    String locationAddress;

    String urlVehicle;

    String urlMile;

    int claimCount;


    public String getPeriod() {

        String strPeriod = "";

        DateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT);
        strPeriod = df.format(dateFrom.getTime()) + " ~ " + df.format(dateTo.getTime());

        return strPeriod;

    }

    public String getDateFromString() {

        String strDate = "";

        DateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT);
        strDate = df.format(dateFrom.getTime());

        return strDate;
    }

    public  String getDateToString() {

        String strDate = "";

        try {
            DateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT);
            strDate = df.format(dateTo.getTime());
        }
        catch (Exception e) {

        }

        return strDate;
    }


    public String getRemainingTime() {

        String strPeriod = getDateToString();

        if (dateFrom != null && dateTo != null) {

            long timeStampFrom = GregorianCalendar.getInstance().getTimeInMillis();

            long timeStampTo = dateTo.getTimeInMillis();

            long remaining = (timeStampTo - timeStampFrom) / 1000;

            strPeriod = String.format("%dd %dh %dm", remaining / 86400, (remaining % 86400) / 3600, (remaining % 3600) / 60);
        }

        return strPeriod;
    }


}
