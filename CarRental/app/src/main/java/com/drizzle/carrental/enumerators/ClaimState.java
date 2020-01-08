package com.drizzle.carrental.enumerators;

public enum ClaimState {

    PENDING_REVIEW("Pending review", 1),
    INCOMPLETE("Incomplete", 2),
    NOT_APPROVED ("Not Approved", 3),
    EXPERT_UNDERGOING("Expert Undergoing...", 4),
    APPROVED ("Approved", 5);

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
