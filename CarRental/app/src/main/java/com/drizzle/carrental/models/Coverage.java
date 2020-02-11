package com.drizzle.carrental.models;

import android.location.Location;

import com.drizzle.carrental.enumerators.CoverageState;
import com.drizzle.carrental.globals.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Coverage {

    Long id;

    String title;

    boolean activeState;

    CoverageState state = null;

    Company company;

    GregorianCalendar dateFrom;

    GregorianCalendar dateTo;

    GregorianCalendar dateOperation;

    long remainingTime = 0;

    Location location;

    String locationAddress;

    String urlVideoVehicle;

    String urlVideoMile;

    String urlImageVehicle;

    String urlImageMile;

    int claimCount;


    public String getPeriod() {

        String strPeriod = "";

        if (dateFrom == null || dateTo == null) {
            return strPeriod;
        }

        DateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT);
        strPeriod = df.format(dateFrom.getTime()) + " ~ " + df.format(dateTo.getTime());

        return strPeriod;

    }

    public String getDateOperationAsString() {

        String strDate = "";

        if (dateOperation == null) {
            return strDate;
        }

        DateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT);
        try {
            strDate = df.format(dateOperation.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return strDate;

    }

    public String getDateFromString() {

        String strDate = "";

        DateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT);
        strDate = df.format(dateFrom.getTime());

        return strDate;
    }

    public String getDateToString() {

        String strDate = "";

        try {
            DateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT);
            strDate = df.format(dateTo.getTime());
        } catch (Exception e) {

        }

        return strDate;
    }


    public String getRemainingTimeAsString() {

        String strPeriod = "";

        if (remainingTime != 0) {

            strPeriod = String.format("%dd %dh %dm", remainingTime / 86400, (remainingTime % 86400) / 3600, (remainingTime % 3600) / 60);
            return strPeriod;
        }

        strPeriod = getDateToString();

        if (dateFrom != null && dateTo != null) {

            long timeStampFrom = GregorianCalendar.getInstance().getTimeInMillis();

            long timeStampTo = dateTo.getTimeInMillis();

            long remaining = (timeStampTo - timeStampFrom) / 1000;

            strPeriod = String.format("%dd %dh %dm", remaining / 86400, (remaining % 86400) / 3600, (remaining % 3600) / 60);
        }

        return strPeriod;
    }


}
