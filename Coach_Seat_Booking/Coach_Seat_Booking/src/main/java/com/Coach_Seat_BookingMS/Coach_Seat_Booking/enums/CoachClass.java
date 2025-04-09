package com.Coach_Seat_BookingMS.Coach_Seat_Booking.enums;

public enum CoachClass {
    AC_B(1500.0),
    AC_S(1400.0),
    SNIGDHA(1300.0),
    F_BERTH(1200.0),
    F_SEAT(1100.0),
    F_CHAIR(1000.0),
    S_CHAIR(900.0),
    SHOVAN(800.0),
    SHULOV(700.0),
    AC_CHAIR(950.0);

    private final double basePrice;

    CoachClass(double basePrice) {
        this.basePrice = basePrice;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public static CoachClass fromString(String coachClass) {
        if (coachClass == null) {
            return null; // Return null if the input string is null
        }
        coachClass = coachClass.trim().toUpperCase(); 

        for (CoachClass cc : CoachClass.values()) {
            if (cc.name().equalsIgnoreCase(coachClass)) {
                return cc;
            }
        }
        return null;
    }


}
