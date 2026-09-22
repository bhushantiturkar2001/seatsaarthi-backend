package com.seatsaarthi.model.enums;

/**
 * Standard Indian Railways Sleeper / 3AC Berth Classifications.
 */
public enum BerthType {
    LOWER("Lower Berth", true),
    MIDDLE("Middle Berth", false),
    UPPER("Upper Berth", false),
    SIDE_LOWER("Side Lower Berth", true),
    SIDE_UPPER("Side Upper Berth", false);

    private final String displayName;
    private final boolean lowerTier; // Useful for Senior Citizen Priority Rule

    BerthType(String displayName, boolean lowerTier) {
        this.displayName = displayName;
        this.lowerTier = lowerTier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isLowerTier() {
        return lowerTier;
    }

    /**
     * Determines standard Indian Railways Sleeper berth type based on seat number (1-72).
     * Sleeper bays repeat every 8 berths:
     * 1,4 = LOWER | 2,5 = MIDDLE | 3,6 = UPPER | 7 = SIDE_LOWER | 8 = SIDE_UPPER
     */
    public static BerthType fromSeatNumber(int seatNumber) {
        if (seatNumber < 1 || seatNumber > 72) {
            throw new IllegalArgumentException("Seat number must be between 1 and 72. Received: " + seatNumber);
        }
        int remainder = seatNumber % 8;
        return switch (remainder) {
            case 1, 4 -> LOWER;
            case 2, 5 -> MIDDLE;
            case 3, 6 -> UPPER;
            case 7 -> SIDE_LOWER;
            case 0 -> SIDE_UPPER; // 8 % 8 == 0
            default -> throw new IllegalStateException("Unexpected remainder: " + remainder);
        };
    }
}
