package com.drizzle.carrental.enumerators;

public enum PaymentState {

    REQUEST("Requested", 1),
    MORE_ACTION("More Action", 2),
    ERROR("Error", 3),
    REFUSED("Refused", 4),
    RECEIVED("Payment success", 5),
    PENDING("Payment success", 6),
    AUTHORISED("Payment success", 7);


    private String stringValue;
    private int intValue;

    private PaymentState(String toString, int value) {
        stringValue = toString;
        intValue = value;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
