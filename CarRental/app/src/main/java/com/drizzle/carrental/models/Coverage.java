package com.drizzle.carrental.models;

import android.location.Location;

import com.drizzle.carrental.enumerators.CoverageState;
import com.drizzle.carrental.globals.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Coverage {

    String title;

    boolean activeState;

    CoverageState state;

    Company company;

    GregorianCalendar dateFrom;

    GregorianCalendar dateTo;

    Location location;

    String locationAddress;

    ArrayList<String> carURLs = new ArrayList<>();

    public String getPeriod() {

        String strPeriod = "";

        DateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT);
        strPeriod = df.format(dateFrom.getTime()) + " ~ " + df.format(dateTo.getTime());

        return strPeriod;

    }




}
