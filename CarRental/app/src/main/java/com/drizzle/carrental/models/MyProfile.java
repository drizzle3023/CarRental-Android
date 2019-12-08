package com.drizzle.carrental.models;

import java.util.GregorianCalendar;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyProfile {

    private String firstName;

    private String lastName;

    private GregorianCalendar birthday;

    private String emailAddress;

    private String phoneNumber;

    private String address;

    private String creditCardNo;

}
