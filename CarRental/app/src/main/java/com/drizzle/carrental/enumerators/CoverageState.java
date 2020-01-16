package com.drizzle.carrental.enumerators;

public enum CoverageState {

    UNCOVERED ("Uncovered", 1),
    COVERED ("Covered", 2),
    CANCELLED ("Cancelled", 3),
    EXPIRED ("Expired", 4);

    private String stringValue;
    private int intValue;

    private CoverageState(String toString, int value) {
        stringValue = toString;
        intValue = value;
    }

    @Override
    public String toString() {
        return stringValue;
    }

    public int getIntValue() {

        return intValue;
    }
}
