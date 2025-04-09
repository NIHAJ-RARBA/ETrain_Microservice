package com.Coach_Seat_BookingMS.Coach_Seat_Booking.dtos;

import java.util.List;

import com.Coach_Seat_BookingMS.Coach_Seat_Booking.enums.PassengerType;

import lombok.Data;

@Data
public class TicketRequest {
    
    public Long userId;
    public Long coachId;
    public List<String> seatNumbers;
    public List<String> passengerNames;
    public List<PassengerType> passengerTypes;
}
