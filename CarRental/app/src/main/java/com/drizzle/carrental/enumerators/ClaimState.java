package com.drizzle.carrental.enumerators;

public enum ClaimState {

    APPROVED ("Approved", 4),
    NOT_APPROVED ("Not Approved", 2),
    EXPERT_UNDERGOING("Expert Undergoing...", 3),
    INCOMPLETE("Incomplete", 1);

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

    public int getIntValue() {
        return intValue;
    }
}
