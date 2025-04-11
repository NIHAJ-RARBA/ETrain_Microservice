package com.Coach_Seat_BookingMS.Coach_Seat_Booking.services;
import org.springframework.stereotype.Service;

import com.Coach_Seat_BookingMS.Coach_Seat_Booking.enums.CoachClass;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.enums.SeatStatus;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.models.Coach;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.models.Seat;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.repositories.CoachRepository;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.repositories.SeatRepository;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.repositories.TicketRepository;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CoachSeatService {

    private final CoachRepository coachRepository;
    public final SeatRepository seatRepository;
    public final TicketRepository   ticketRepository;

    public final SeatLockService seatLockService;


    
    public List<Coach> getAllCoaches() {
        return coachRepository.findAll();
    }

    
    public Optional<Coach> getCoachById(Long id) {
        return coachRepository.findById(id);
    }
    
    
 
    public List<Coach> getCoachesByClass(String coachClass) {

        if (coachClass == null) {
            return new ArrayList<>(); // Return an empty list if coachClass is null
        }

        return coachRepository.findByCoachClass(CoachClass.fromString(coachClass));
    }
       

    public List<Coach> getCoachesByRouteAndClass(Long routeId, String coachClass) {

        return coachRepository.findByRouteIdAndCoachClass(routeId, CoachClass.fromString(coachClass));
    }


    public List<Coach> getCoachesByRouteId(Long routeId) {
        return coachRepository.findByRouteId(routeId);
    }



    public Coach createCoach(Coach coach) {
        // Validate input data
        if (coach.getCoachClass() == null) {
            throw new IllegalArgumentException("Coach class cannot be null");
        }
    
        Double seatPrice = coach.getSeatPrice();
        System.out.println("TOTAL SEATS: " + coach.getTotalSeats());
    
        // Set the default number of rows in a coach if not provided
        int numRows = calculateNumRows(coach.getTotalSeats());
    
        // Save the coach to generate its ID
        coach = coachRepository.save(coach);
    
        // Generate seats using the extracted method
        List<Seat> seats = createSeatsForCoach(coach, numRows, seatPrice);
    
        // Associate the seats with the coach
        coach.setSeats(seats);
        coachRepository.save(coach); // Save the coach to update the seats relationship
        seatRepository.saveAll(seats); // Save all seats to the database
    
        return coach;
    }
    

    private int calculateNumRows(int totalSeats) {
        if (totalSeats == 0) {
            // Default 15 rows in a coach (A to O)
            return 15;
        } else {
            // 4 seats in each row
            int numRows = totalSeats / 4;
            numRows += totalSeats % 4 == 0 ? 0 : 1; // Add an extra row if there are remaining seats
            if (numRows > 26) {
                throw new IllegalArgumentException("Total seats cannot exceed 104 (26 rows of 4 seats each)");
            }
            return numRows;
        }
    }
    

    private List<Seat> createSeatsForCoach(Coach coach, int numRows, Double seatPrice) {
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= numRows; i++) { // Loop through rows (A to O)
            char rowLetter = (char) ('A' + i - 1); // Convert row number to a letter (A, B, C, ..., O)
    
            for (int seatInRow = 1; seatInRow <= 4; seatInRow++) { // Four seats per row
                String seatNumber = rowLetter + String.valueOf(seatInRow); // Generate seat number (e.g., A1, A2, B1, B2)
    
                // Create a new seat
                Seat seat = new Seat();
                seat.setStatus(SeatStatus.AVAILABLE); // Set the seat status to available
                seat.setBookingType("Online+Counter");
                seat.setSeatNumber(seatNumber);
                seat.setCoachId(coach.getCoachId());
                seat.setPrice(seatPrice);
    
                // Add the seat to the list
                seats.add(seat);
            }
        }


        
        return seats;
    }

    

    public Coach updateCoachSeats(Long id, Coach coachDetails) {

        Optional<Coach> coach = coachRepository.findById(id);

        if (coach.isPresent()) {

            Coach existingCoach = coach.get();


            List<Seat> existingSeats = existingCoach.getSeats();
            
            List<Seat> newSeats = createSeatsForCoach(existingCoach, calculateNumRows(coachDetails.getTotalSeats()), coachDetails.getSeatPrice());
            
            for (Seat seat : existingSeats) {
                seatRepository.deleteById(seat.getSeatId());
            }
            // Clear the existing seats and add new ones
            existingCoach.getSeats().clear();
            existingCoach.getSeats().addAll(newSeats);
            existingCoach.setTotalSeats(newSeats.size());
            


            coachRepository.save(existingCoach);
            seatRepository.saveAll(newSeats);

            return existingCoach;

        }
        else {
            throw new RuntimeException("Coach not found with ID: " + id);
        }

    }
    
    
    
    public boolean deleteAllCoaches() {
        if (coachRepository.count() > 0) {
            ticketRepository.deleteAll();
            coachRepository.deleteAll();
            return true;
        } else {
            return false;
        }
    }
    
    
    
    public boolean deleteCoachById(Long id) {
        Optional<Coach> coach = coachRepository.findById(id);
        if (coach.isPresent()) {
            List<Seat> seatList = coach.get().getSeats();
            ticketRepository.deleteAllByCoach(coach.get());
            seatRepository.deleteAll(seatList);
            coachRepository.deleteById(id);
            return true;
        }
        else{
            return false;
        }
    }
    
    

    // public List<Boolean> getUnavailableSeats(Long coachId) {

    //     Optional<Coach> coach = coachRepository.findById(coachId);
    //     if (coach.isPresent()) {
    //         return seatLockService.whichSeatsAreLocked(coach.get().getSeats());
    //     } else {
    //         throw new RuntimeException("Coach not found with ID: " + coachId);
    //     }
    // }

    public List<Seat> getSeatsByCoachId(Long coachId) {

        Optional<Coach> coach = coachRepository.findById(coachId);
        if (coach.isEmpty()) {
            throw new RuntimeException("Coach not found with ID: " + coachId);
        }
        return seatRepository.findByCoachId(coach.get().getCoachId());
    }

}
