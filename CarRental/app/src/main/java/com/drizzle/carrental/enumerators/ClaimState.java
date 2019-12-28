package com.drizzle.carrental.enumerators;

public enum ClaimState {

    APPROVED ("Approved", 0),
    NOT_APPROVED ("Not Approved", 1),
    EXPERT_UNDERGOING("Expert Undergoing...", 2),
    INCOMPLETE("Incomplete", 3);

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
