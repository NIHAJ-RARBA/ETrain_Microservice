package com.Coach_Seat_BookingMS.Coach_Seat_Booking.services;
import org.springframework.stereotype.Service;

import com.Coach_Seat_BookingMS.Coach_Seat_Booking.models.Coach;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.models.Passengers;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.models.Seat;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.models.Ticket;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.repositories.CoachRepository;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.repositories.TicketRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final TicketRepository ticketRepository;
    private final CoachRepository coachRepository;

    private final SeatLockService seatLockService;


    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Optional<Ticket> getTicketById(Long ticketId) {
        return ticketRepository.findById(ticketId);
    }

    public List<Ticket> getTicketsByUserId(Long userId) {
        return ticketRepository.findAllByUserId(userId).get();
    }

    public List<Ticket> getTicketsByUserIdAndCoachId(Long userId, Long coachId) {
        Coach coach = coachRepository.findById(coachId).orElse(null);
        if (coach == null) {
            throw new RuntimeException("Coach not found with ID: " + coachId);
        }

        return ticketRepository.findAllByUserIdAndCoach(userId, coach).get();
        
    }


    public Optional<Ticket> createTicketForUser(Long userId, Long CoachId, List<Seat> seats, List<Passengers> passengers) {
        
        Coach coach = coachRepository.findById(CoachId).orElseThrow(() -> new RuntimeException("Coach not found with ID: " + CoachId));
        if (coach == null) {
            throw new RuntimeException("Coach not found with ID: " + CoachId);
        }

        Ticket ticket = new Ticket(userId, coach, seats, passengers);
        ticket.setPaid(false);

        // temporary lock on seats
        seatLockService.lockSeatsForUser(seats.stream().map(Seat::getSeatId).toArray(Long[]::new), userId);
        
        return Optional.of(ticketRepository.save(ticket));
    }


    public boolean cancelTicket(Long ticketId) {
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        if (ticket.isEmpty()) {
            return false;
        }

        // release seats
        seatLockService.unlockSeats(ticket.get().getSeats().stream().map(Seat::getSeatId).toArray(Long[]::new)); // unlock seats

        ticketRepository.delete(ticket.get()); // delete ticket

        return true;
    }

    
    public boolean payTicket(Long ticketId) {
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        if (ticket.isEmpty()) {
            return false;
        }

        if (ticket.get().getPassengers().isEmpty() || ticket.get().getSeats().isEmpty()) {
            return false;
        }

        ticket.get().setPaid(true);
        ticketRepository.save(ticket.get());
        return true;
    }

    public boolean isTicketPaid(Long ticketId) {
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        if (ticket.isEmpty()) {
            return false;
        }
        return ticket.get().isPaid();
    }

    public boolean updateTicket(Long ticketId, List<Seat> seats, List<Passengers> passengers) {
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        if (ticket.isEmpty()) {
            return false;
        }

        ticket.get().setPassengers(passengers);
        ticket.get().setSeats(seats);
        ticketRepository.save(ticket.get());
        return true;
    }

    public Double getPayableAmount(Long ticketId) {
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        if (ticket.isEmpty()) {
            return 0.0;
        }
        return ticket.get().getTotalAmount();
    }

}

