package com.drizzle.carrental.enumerators;

public enum DamagedPart {

    LEFT_FENDER_PANEL("damaged_zone_left_fender_panel", 0),
    LEFT_FRONT_DOOR("damaged_zone_left_front_door", 1),
    LEFT_QUARTER_PANEL("damaged_zone_left_quarter_panel", 2),
    LEFT_HOOD("damaged_zone_left_hood", 3),
    RIGHT_HOOD("damaged_zone_right_hood", 4),
    RIGHT_ROOF("damaged_zone_right_roof", 5),
    LEFT_ROOF("damaged_zone_left_roof", 6),
    LEFT_BACK("damaged_zone_left_back", 7),
    RIGHT_BACK("damaged_zone_right_back", 8),
    RIGHT_FENDER_PANEL("damaged_zone_right_fender_panel", 9),
    RIGHT_FRONT_DOOR("damaged_zone_right_front_door", 10),
    RIGHT_QUARTER_PANEL("damaged_zone_right_quarter_panel", 11);

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

    public static DamagedPart fromString(String text) {
        for (DamagedPart b : DamagedPart.values()) {
            if (b.stringValue.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }

}
