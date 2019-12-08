package com.drizzle.carrental.enumerators;

public enum ClaimState {
    COVERED ("Covered", 0),
    UNCOVERED ("Uncovered", 1);

    private String stringValue;
    private int intValue;

    private ClaimState(String toString, int value) {
        stringValue = toString;
        intValue = value;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
