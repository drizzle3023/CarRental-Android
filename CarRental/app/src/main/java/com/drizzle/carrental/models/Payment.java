package com.drizzle.carrental.models;

import com.drizzle.carrental.enumerators.PaymentState;
import com.drizzle.carrental.globals.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Payment {


    String title;

    PaymentState state;

    String information;

    GregorianCalendar paymentDate;

    public  String getPaymentDateAsString() {

        DateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT);
        String strDate = df.format(paymentDate.getTime());

        return strDate;
    }

}
