package com.Coach_Seat_BookingMS.Coach_Seat_Booking.controllers;

import com.Coach_Seat_BookingMS.Coach_Seat_Booking.dtos.IdRequest;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.dtos.TicketRequest;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.dtos.UserCoachIdRequest;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.enums.SeatStatus;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.models.Passengers;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.models.Seat;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.models.Ticket;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.services.BookingService;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.services.CoachSeatService;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.services.SeatLockService;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/booking")
public class BookingController {

    private class SeatPassengerListList
    {
        public SeatPassengerListList(List<Seat> seatsInTicket2, List<Passengers> passengers2) {
            this.seatsInTicket = seatsInTicket2;
            this.passengers = passengers2;
        }
        List<Seat> seatsInTicket;
        List<Passengers> passengers;
    }

    private final BookingService bookingService;
    private final CoachSeatService coachSeatService;

    private final SeatLockService seatLockService;

    public SeatPassengerListList seatAndPassengerListCreation(TicketRequest request) {
        List<Seat> seatsInCoach = coachSeatService.getSeatsByCoachId(request.getCoachId());
        List<Passengers> passengers = new ArrayList<>();

        List<String> seatNumbers = request.getSeatNumbers();
        seatNumbers.sort((a, b) -> a.compareTo(b));

        int row = seatNumbers.get(0).substring(0, 1).charAt(0) - 'A';
        row = row + 1; // Convert to 1-based index

        if (row > seatsInCoach.size()) {
            return new SeatPassengerListList(null, null);
        }

        List<Seat> seatsInTicket = seatsInCoach.stream().filter(
            seat -> request.getSeatNumbers().contains(seat.getSeatNumber())
            ).toList();
        
        

        for (Seat seat : seatsInTicket) {
            if (seat.getStatus() == SeatStatus.UNAVAILABLE) {
                new ResponseEntity<>(null, HttpStatus.CONFLICT);
            }
        }

        


        for (int i = 0; i < request.getPassengerNames().size(); i++) {
            Passengers passenger = new Passengers();
            passenger.setTicket(null);
            passenger.setName(request.getPassengerNames().get(i));
            passenger.setType(request.getPassengerTypes().get(i));
            passengers.add(passenger);

            
        }

        // System.out.println("SEATS IN TICKET: " + seatsInTicket);
        // System.out.println("PASSENGERS IN TICKET: " + passengers);
        
        return new SeatPassengerListList(seatsInTicket, passengers);

    }


    @PostMapping("/create-ticket")
    public ResponseEntity<Ticket> createTicket(@RequestBody TicketRequest bookingRequest) {
        
        if (bookingRequest.getSeatNumbers().size() != bookingRequest.getPassengerNames().size() 
            || bookingRequest.getSeatNumbers().size() != bookingRequest.getPassengerTypes().size()
            || bookingRequest.getPassengerNames().size() != bookingRequest.getPassengerTypes().size()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            
        }

        


        SeatPassengerListList map = seatAndPassengerListCreation(bookingRequest);        

        List<Seat> seatsInTicket = map.seatsInTicket;
        List<Passengers> passengers = map.passengers;

        if (seatsInTicket == null || passengers == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        if (seatsInTicket.size() == 0 || passengers.size() == 0) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        // System.out.println("SEATS IN TICKET: " + seatsInTicket);
        // System.out.println("PASSENGERS IN TICKET: " + passengers);

        for (Seat seat : seatsInTicket) {
            if (seat.getStatus() == SeatStatus.UNAVAILABLE || seatLockService.isSeatLocked(seat.getSeatId(), bookingRequest.getUserId())) {
                System.err.println("Seat " + seat.getSeatId() + " is already booked or locked by another user.");
                return new ResponseEntity<>(null, HttpStatus.CONFLICT);
            }
        }



        Ticket ticket = bookingService.createTicketForUser(bookingRequest.getUserId(), bookingRequest.getCoachId(), seatsInTicket, passengers).orElse(null);
        
        // passengerRepository.saveAll(passengers);

        if (ticket == null) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(ticket, HttpStatus.CREATED);

    }
    

    @DeleteMapping("/cancel")
    public ResponseEntity<String> cancelTicketById(@RequestBody IdRequest idRequest) {
    
        Long id = idRequest.getId();
        Optional<Ticket> ticket = bookingService.getTicketById(id);
        if (ticket.isPresent()) {
            if(bookingService.cancelTicket(id))
            {
                return new ResponseEntity<>("Ticket cancelled successfully",HttpStatus.OK);
            }
            else
            {
                return new ResponseEntity<>("Failed to cancel ticket",HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>("Ticket not found",HttpStatus.NOT_FOUND);
        }
    
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateTicket(@RequestBody TicketRequest ticketRequest) {
        
        List<Ticket> tickets = bookingService.getTicketsByUserIdAndCoachId(ticketRequest.getUserId(), ticketRequest.getCoachId());
        
        if (tickets.isEmpty()) {
            return new ResponseEntity<>("Ticket not found",HttpStatus.NOT_FOUND);
        }

        else
        {
            Ticket ticket = tickets.get(0);
            SeatPassengerListList map = seatAndPassengerListCreation(ticketRequest);

            List<Seat> seatsInTicket = map.seatsInTicket;
            List<Passengers> passengers = map.passengers;

            if (bookingService.updateTicket(ticket.getTicketId(), seatsInTicket, passengers)) {
                return new ResponseEntity<>("Ticket updated successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Failed to update ticket", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

    }

    @PutMapping("/paid")
    public ResponseEntity<String> markTicketAsPaid(@RequestBody IdRequest idRequest) 
    {
        Long ticketId = idRequest.getId();
        if (bookingService.payTicket(ticketId)) {
            return new ResponseEntity<>("Ticket marked as paid", HttpStatus.OK);
        } 
        else {
            return new ResponseEntity<>("Failed to mark ticket as paid", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }        


    @GetMapping("/all")
    public ResponseEntity<List<Ticket>> getAllTickets() {
        List<Ticket> tickets = bookingService.getAllTickets();
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    @GetMapping("/byUserId")
    public ResponseEntity<List<Ticket>> getTicketsByUserId(@RequestBody IdRequest idRequest) {
        Long userId = idRequest.getId();
        List<Ticket> tickets = bookingService.getTicketsByUserId(userId);
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    @GetMapping("/byTicketId")
    public ResponseEntity<Ticket> getTicketById(@RequestBody IdRequest idRequest) {
        Long ticketId = idRequest.getId();
        Optional<Ticket> ticket = bookingService.getTicketById(ticketId);
        if (ticket.isPresent()) {
            return new ResponseEntity<>(ticket.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/byUserIdAndCoachId")
    public ResponseEntity<List<Ticket>> getTicketsByUserIdAndCoachId(@RequestBody UserCoachIdRequest ticketRequest) {
        Long userId = ticketRequest.getUserId();
        Long coachId = ticketRequest.getCoachId();
        List<Ticket> tickets = bookingService.getTicketsByUserIdAndCoachId(userId, coachId);
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }






}