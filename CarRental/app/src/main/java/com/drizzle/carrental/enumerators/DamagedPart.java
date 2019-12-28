package com.drizzle.carrental.enumerators;

public enum DamagedPart {

    LEFT_FENDER_PANEL("Left Fender Panel", 0),
    LEFT_FRONT_DOOR("Left Front Door", 1),
    LEFT_QUARTER_PANEL("Left Quarter Panel", 2),
    LEFT_HOOD("Left Hood", 3),
    RIGHT_HOOD("Right Hood", 4),
    RIGHT_ROOF("Right Hood", 5),
    LEFT_ROOF("Left Roof", 6),
    LEFT_BACK("Left Back", 7),
    RIGHT_BACK("Right Back", 8),
    RIGHT_FENDER_PANEL("Right Fender Panel", 9),
    RIGHT_FRONT_DOOR("Right Front Door", 10),
    RIGHT_QUARTER_PANEL("Left Quarter Panel", 11);

    private String stringValue;
    private int intValue;

    private DamagedPart(String toString, int value) {
        stringValue = toString;
        intValue = value;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
