package com.Coach_Seat_BookingMS.Coach_Seat_Booking.repositories;

import com.Coach_Seat_BookingMS.Coach_Seat_Booking.models.Coach;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.models.Seat;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    // Custom query methods can be defined here if needed
    // For example, to find a seat by its number:
    // Optional<Seat> findBySeatNumber(String seatNumber);

    
    List<Seat> findByCoach(Coach coach);
    Optional<Seat> findByCoachAndSeatNumber(Coach coach, String seatNumber);
}