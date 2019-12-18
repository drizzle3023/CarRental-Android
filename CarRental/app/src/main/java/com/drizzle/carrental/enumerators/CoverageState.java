package com.drizzle.carrental.enumerators;

public enum CoverageState {
    COVERED ("Covered", 0),
    UNCOVERED ("Uncovered", 1);

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
}
