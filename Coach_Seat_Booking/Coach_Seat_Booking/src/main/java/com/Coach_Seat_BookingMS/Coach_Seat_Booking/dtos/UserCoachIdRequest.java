package com.Coach_Seat_BookingMS.Coach_Seat_Booking.dtos;


import lombok.Data;

@Data
public class UserCoachIdRequest {
    
    public Long userId;
    public Long coachId;
}