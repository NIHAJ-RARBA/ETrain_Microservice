package com.Coach_Seat_BookingMS.Coach_Seat_Booking.repositories;

import com.Coach_Seat_BookingMS.Coach_Seat_Booking.models.Coach;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.models.Ticket;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> 
{
    Optional<Ticket> findByUserId(Long userId);
    List<Ticket> findByIsPaidFalseAndCreatedAtBefore(LocalDateTime time);

    Optional<List<Ticket>> findAllByUserId(Long userId);
    Optional<List<Ticket>> findAllByUserIdAndCoach(Long userId, Coach coach);
    
    void deleteAllByCoach(Coach coach);

}