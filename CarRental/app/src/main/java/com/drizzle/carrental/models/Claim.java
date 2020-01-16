package com.drizzle.carrental.models;

import android.location.Location;

import com.drizzle.carrental.enumerators.ClaimState;
import com.drizzle.carrental.enumerators.DamagedPart;
import com.drizzle.carrental.globals.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Claim {

    long id = 0; //id of claim

    String whatHappened; //answer for "What happened"

    GregorianCalendar whenHappened; //answer for "When this happened"

    Location whereHappened; //answer for "Where this happened"

    String addressHappened; //address of happened location

    List<DamagedPart> damagedParts; //damaged part

    String videoURL; //url of recorded video

    String imageURL; //url of recorded video

    String extraDescription; //extra information

    ClaimState claimState; //claim state

    public String getDateString() {

        String strDate = "";

        DateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_FOR_CLAIM);
        if (whenHappened != null) {
            strDate = df.format(whenHappened.getTime());
        }
        return strDate;

    }

    public String getDamagedPartsString() {

        String strDamagedParts = "";

        if (damagedParts != null) {
            for (int i = 0; i < damagedParts.size(); i++) {

                strDamagedParts = strDamagedParts + damagedParts.get(i).toString() + ",";
            }

            if (!strDamagedParts.isEmpty()) {
                strDamagedParts = strDamagedParts.substring(0, strDamagedParts.length() - 1);
            }
        }
        return strDamagedParts;
    }

    public void setDamagedPartsFromString(String strPartList) {
        damagedParts = new ArrayList<DamagedPart>();


        if(strPartList.isEmpty()) {
            return;
        }

        String[] stringArrayList = strPartList.split(",");
        if (stringArrayList.length == 0) {
            return;
        }
        else {

            for (int i = 0; i < stringArrayList.length; i ++) {
                damagedParts.add(DamagedPart.fromString(stringArrayList[i]));
            }
        }
    }
}
