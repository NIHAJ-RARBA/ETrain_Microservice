package com.Coach_Seat_BookingMS.Coach_Seat_Booking.repositories;

import com.Coach_Seat_BookingMS.Coach_Seat_Booking.models.Seat;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    // Custom query methods can be defined here if needed
    // For example, to find a seat by its number:
    // Optional<Seat> findBySeatNumber(String seatNumber);

    
    List<Seat> findByCoachId(Long coachId);
    Optional<Seat> findByCoachIdAndSeatNumber(Long coachId, String seatNumber);

    void deleteAllByCoachId(Long coachId);

}