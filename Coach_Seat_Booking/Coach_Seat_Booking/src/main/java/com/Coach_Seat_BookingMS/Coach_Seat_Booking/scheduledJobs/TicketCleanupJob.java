package com.Coach_Seat_BookingMS.Coach_Seat_Booking.scheduledJobs;

import com.Coach_Seat_BookingMS.Coach_Seat_Booking.models.Seat;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.enums.SeatStatus;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.models.Ticket;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.repositories.SeatRepository;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.repositories.TicketRepository;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.services.SeatLockService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TicketCleanupJob {

    private final TicketRepository ticketRepository;
    private final SeatRepository seatRepository;
    private final SeatLockService seatLockService;

    @Scheduled(fixedRate = 60000) // every 1 min = 60000ms
    public void expireUnpaidTickets() {
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);
        List<Ticket> expiredTickets = ticketRepository.findByIsPaidFalseAndCreatedAtBefore(tenMinutesAgo);

        for (Ticket ticket : expiredTickets) {
            List<Seat> seats = ticket.getSeats();
            
            // Release seats
            for (Seat seat : seats) {
                seat.setStatus(SeatStatus.AVAILABLE);
                seatRepository.save(seat);

                // remove Redis lock
                seatLockService.unlockSeat(seat.getSeatId());
            }

            ticketRepository.delete(ticket); // remove ticket
        }
    }
}
