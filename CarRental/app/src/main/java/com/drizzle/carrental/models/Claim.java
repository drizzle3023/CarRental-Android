package com.drizzle.carrental.models;

import android.location.Location;

import com.drizzle.carrental.enumerators.ClaimState;
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
public class Claim {

    String title;

    boolean activeState;

    ClaimState state;

    GregorianCalendar dateFrom;

    GregorianCalendar dateTo;

    String location;

    ArrayList<String> carURLs = new ArrayList<>();

    public String getPeriod() {

        String strPeriod = "";

        DateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT);
        strPeriod = df.format(dateFrom.getTime()) + df.format(dateTo.getTime());

        return strPeriod;

    }

}
