package com.Coach_Seat_BookingMS.Coach_Seat_Booking.enums;

public enum SeatStatus {
    AVAILABLE("AVAILABLE"),
    UNAVAILABLE("UNAVAILABLE");

    private final String status;

    SeatStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
