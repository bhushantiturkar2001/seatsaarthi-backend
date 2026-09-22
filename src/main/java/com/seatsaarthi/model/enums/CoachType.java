package com.seatsaarthi.model.enums;

/**
 * Indian Railways Coach Classifications and Capacities.
 */
public enum CoachType {
    SLEEPER("Sleeper Class (Non-AC)", 72, "S"),
    AC_3_TIER("AC 3 Tier", 72, "B"),
    AC_2_TIER("AC 2 Tier", 54, "A"),
    AC_FIRST_CLASS("AC First Class", 24, "H"),
    CHAIR_CAR("AC Chair Car", 78, "C");

    private final String displayName;
    private final int defaultCapacity;
    private final String coachPrefix;

    CoachType(String displayName, int defaultCapacity, String coachPrefix) {
        this.displayName = displayName;
        this.defaultCapacity = defaultCapacity;
        this.coachPrefix = coachPrefix;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDefaultCapacity() {
        return defaultCapacity;
    }

    public String getCoachPrefix() {
        return coachPrefix;
    }
}
