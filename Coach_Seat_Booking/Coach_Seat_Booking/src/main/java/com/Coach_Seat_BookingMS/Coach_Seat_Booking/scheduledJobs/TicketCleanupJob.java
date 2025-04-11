package com.Coach_Seat_BookingMS.Coach_Seat_Booking.scheduledJobs;

import com.Coach_Seat_BookingMS.Coach_Seat_Booking.models.Passengers;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.models.Seat;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.enums.SeatStatus;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.models.Ticket;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.repositories.PassengerRepository;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.repositories.SeatRepository;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.repositories.TicketRepository;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.services.SeatLockService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TicketCleanupJob {

    private final TicketRepository ticketRepository;
    private final SeatRepository seatRepository;
    private final SeatLockService seatLockService;
    private final PassengerRepository passengerRepository;

    @Scheduled(fixedRate = 60000) // every 1 min = 60000ms
    @Transactional
    public void expireUnpaidTickets() {
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(1);
        List<Ticket> expiredTickets = ticketRepository.findByIsPaidFalseAndCreatedAtBefore(tenMinutesAgo);

        for (Ticket ticket : expiredTickets) {
            List<Seat> seats = ticket.getSeats();

            if (seats != null) {
                for (Seat seat : seats) {
                    seatLockService.unlockSeat(seat.getSeatId(), ticket.getUserId());
                    seat.setTicket(null); // remove ticket reference from seat
                    seat.setStatus(SeatStatus.AVAILABLE);
                    seatRepository.save(seat); // important: persist the change
                }
            }

            // Clean up passengers (if needed)
            List<Passengers> passengers = ticket.getPassengers();
            if (passengers != null && !passengers.isEmpty()) {
                passengerRepository.deleteAll(passengers);
            }

            // Now that all links are broken, safely delete the ticket
            ticketRepository.delete(ticket);

            System.out.println("Expired ticket cleaned up: " + ticket.getTicketId());
        }

        // Optionally, you can log the number of expired tickets cleaned up
        System.out.println("Expired tickets cleaned up: " + expiredTickets.size());    
    
    }
}
