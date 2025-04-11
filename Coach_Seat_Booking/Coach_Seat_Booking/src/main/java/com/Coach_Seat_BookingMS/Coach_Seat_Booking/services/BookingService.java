package com.Coach_Seat_BookingMS.Coach_Seat_Booking.services;
import org.springframework.stereotype.Service;

import com.Coach_Seat_BookingMS.Coach_Seat_Booking.enums.SeatStatus;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.models.Coach;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.models.Passengers;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.models.Seat;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.models.Ticket;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.repositories.CoachRepository;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.repositories.PassengerRepository;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.repositories.SeatRepository;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.repositories.TicketRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final TicketRepository ticketRepository;
    private final CoachRepository coachRepository;
    public final SeatRepository seatRepository;
    public final PassengerRepository passengerRepository;

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

        if (seats.isEmpty()) {
            System.out.println("No seats selected for booking.");
            return Optional.empty();
        }

        if (passengers.isEmpty()) {
            System.out.println("No passengers selected for booking.");
            return Optional.empty();
        }

        // Check if the seats are already booked
        for (Seat seat : seats) {
            if (seatLockService.isSeatLocked(seat.getSeatId(), userId)) {
                System.out.println("Seat " + seat.getSeatId() + " is already booked by user: " + userId);
                System.out.println("Seat " + seat.getSeatId() + " is already booked.");                
                return Optional.empty();
            }
        }

        // System.err.println(seats);

        System.out.println("Creating ticket for user: " + userId + " with seats: " + seats + " and passengers: " + passengers);

        Ticket ticket = new Ticket();
        ticket.setUserId(userId);
        ticket.setCoach(coach);
        ticket.setSeats(new ArrayList<>(seats));
        ticket.setPaid(false);
        ticket.setCreatedAt(java.time.LocalDateTime.now());
        
        ticket.setTotalAmount(ticket.getTotalAmount()); // Calculate total amount based on seats and passengers
        // ticketRepository.save(ticket);

        for (Seat seat : ticket.getSeats()) {
            
            if (seat.getTicket() != null)
            {
                System.out.println("Seat " + seat.getSeatId() + " is already booked.");
                return Optional.empty();
            }
            
            seat.setTicket(ticket);
            // seatRepository.save(seat); // Save the seat with the ticket ID
        }
        
        
        
        // temporary lock on seats
        if (!seatLockService.lockSeatsForUser(seats.stream().map(Seat::getSeatId).toArray(Long[]::new), userId)) {
            return Optional.empty();
        }
        
        for (Passengers passenger : passengers) {
            if (passenger.getTicket() != null) {
                System.out.println("Passenger " + passenger.getName() + " is already in another ticket.");
                return Optional.empty();
            }
            passenger.setTicket(ticket);
            // passengerRepository.save(passenger); // Save the passenger with the ticket ID
        }
        
        ticket.setSeats(new ArrayList<>(seats));
        ticket.setPassengers(new ArrayList<>(passengers));  




        
        
        return Optional.of(ticketRepository.save(ticket));
    }


    @Transactional
    public boolean cancelTicket(Long ticketId) {
        Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);
        if (optionalTicket.isEmpty()) {
            return false;
        }
    
        Ticket ticket = optionalTicket.get();
    
        // Unlock seats
        List<Seat> seats = ticket.getSeats();
        if (seats != null) {
            for (Seat seat : seats) {
                seatLockService.unlockSeat(seat.getSeatId(), ticket.getUserId());
                seat.setTicket(null); // remove ticket reference
                seat.setStatus(SeatStatus.AVAILABLE);
                seatRepository.save(seat);
            }
        }
    
        // Break relationships properly
        ticket.getSeats().clear();
        ticket.getPassengers().clear();
    
        // Delete passengers if needed
        passengerRepository.deleteAll(ticket.getPassengers());
    
        // Now delete ticket
        ticketRepository.delete(ticket);
    
        return true;
    }

    
    @Transactional
    public boolean payTicket(Long ticketId) {

        Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);
        if (optionalTicket.isEmpty()) {
            System.out.println("Ticket not found with ID: " + ticketId);
            return false;
        }
        
        Ticket ticket = optionalTicket.get();
        
        if (ticket.isPaid()) {
            System.out.println("Ticket is already paid. Ticket ID: " + ticketId);
            return false;
        }

        // if (ticket.getPassengers().isEmpty() || ticket.getSeats().isEmpty()) {
        //     System.out.println("Ticket is not valid. Please select seats and passengers before paying.");
        //     return false;
        // }

        List<Seat> seats = ticket.getSeats();
        for (Seat seat : seats) 
        {
            if (seat.getStatus() != SeatStatus.UNAVAILABLE) {
                System.out.println("Seat " + seat.getSeatId() + " is not booked. Please select seats before paying.");
                return false;
            }
        }

        ticket.setPaid(true);
        ticketRepository.save(ticket);
        return true;
    }

    public boolean isTicketPaid(Long ticketId) {
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        if (ticket.isEmpty()) {
            return false;
        }
        return ticket.get().isPaid();
    }

    @Transactional
    public boolean updateTicket(Long ticketId, List<Seat> seats, List<Passengers> passengers) {
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        if (ticket.isEmpty()) {
            return false;
        }

        // if any of the seats are already booked, return false
        for (Seat seat : seats) {
            if (seat.getStatus() == SeatStatus.UNAVAILABLE || seatLockService.isSeatLocked(seat.getSeatId(), ticket.get().getUserId())) {
                System.err.println("Seat " + seat.getSeatId() + " is already booked or locked by another user.");
                return false;
            }
        }



        List<Seat> oldSeats = ticket.get().getSeats();
        // get the seats that are not present in seats
        List<Seat> seatsToUnlock = oldSeats.stream().filter(seat -> !seats.contains(seat)).toList();
        List<Seat> seatsToLock = seats.stream().filter(seat -> !oldSeats.contains(seat)).toList();
        
        if(!seatsToUnlock.isEmpty()) {
            seatLockService.unlockSeats(ticket.get().getSeats(), ticket.get().getUserId()); // unlock old seats
        }
        
        
        if(!seatsToLock.isEmpty()) {
            seatLockService.lockSeatsForUser(seatsToLock.stream().map(Seat::getSeatId).toArray(Long[]::new), ticket.get().getUserId()); // lock new seats

        }

        // update the ticket with new seats and passengers
        ticket.get().setSeats(new ArrayList<>(seats));
        ticket.get().setPassengers(new ArrayList<>(passengers));
        ticket.get().setTotalAmount(ticket.get().getTotalAmount()); // Calculate total amount based on seats and passengers
        ticket.get().setPaid(false); // set paid to false

        ticketRepository.save(ticket.get());

        for (Seat seat : ticket.get().getSeats()) {
            if (seat.getTicket() != null) {
                System.out.println("Seat " + seat.getSeatId() + " is already booked.");
                return false;
            }

            seat.setTicket(ticket.get());
            seat.setStatus(SeatStatus.UNAVAILABLE); // set status to booked
            seatRepository.save(seat); // Save the seat with the ticket ID
        }

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

