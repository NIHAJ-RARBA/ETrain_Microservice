package com.Coach_Seat_BookingMS.Coach_Seat_Booking.enums;

public enum PassengerType {
    ADULT(300.00),
    CHILD(100.00);

    private final double price;

    PassengerType(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }
}