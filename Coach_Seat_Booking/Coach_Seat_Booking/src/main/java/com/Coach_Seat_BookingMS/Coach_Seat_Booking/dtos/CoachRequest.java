    package com.Coach_Seat_BookingMS.Coach_Seat_Booking.dtos;

    import lombok.Data;

    @Data
    public class CoachRequest {
        
        private Long routeId; // Foreign key to Route (stored as plain Long)
        private Long trainId; // Foreign key to Train (stored as plain Long)
        private String coachClass;
        private int totalSeats; // Total number of seats in the coach
        

    }
