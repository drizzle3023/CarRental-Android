package com.drizzle.carrental.enumerators;

public enum PaymentState {

    SUCCESS("Success", 0),
    FAIL("Fail", 1);

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
