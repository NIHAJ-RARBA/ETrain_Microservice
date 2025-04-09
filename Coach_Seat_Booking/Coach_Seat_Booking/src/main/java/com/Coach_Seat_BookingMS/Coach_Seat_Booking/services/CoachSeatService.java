package com.Coach_Seat_BookingMS.Coach_Seat_Booking.services;
import org.springframework.stereotype.Service;

import com.Coach_Seat_BookingMS.Coach_Seat_Booking.enums.CoachClass;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.enums.SeatStatus;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.models.Coach;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.models.Seat;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.repositories.CoachRepository;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.repositories.SeatRepository;






import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CoachSeatService {

    private final CoachRepository coachRepository;
    public final SeatRepository seatRepository;

    public final SeatLockService seatLockService;

    private int NUMROWS; 

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

        if (coach.getCoachClass() == null) {
            throw new IllegalArgumentException("Coach class cannot be null");
        }

        Double seatPrice = coach.getSeatPrice();

        // Set the default number of rows in a coach if not provided
        if (coach.getTotalSeats() == 0) {
            // default 15 rows in a coach (A to O)
            NUMROWS = 15;             
        } else {
            // 2 seats in each row
            NUMROWS = coach.getTotalSeats() / 2; 
        }
        
        // coach.setTotalSeats(NUMROWS * 2); // Set the total number of seats in the coach
        
        List<Seat> seats = new ArrayList<>(); 
        for (int i = 1; i <= NUMROWS; i++) { // Loop through rows (A to O)
            char rowLetter = (char) ('A' + i - 1); // Convert row number to a letter (A, B, C, ..., O)
    
            for (int seatInRow = 1; seatInRow <= 2; seatInRow++) { // Two seats per row
                String seatNumber = rowLetter + String.valueOf(seatInRow); // Generate seat number (e.g., A1, A2, B1, B2)
    
                // Create a new seat
                Seat seat = new Seat();
                seat.setStatus(SeatStatus.AVAILABLE); // Set the seat status to available
                seat.setBookingType("Online+Counter");
                seat.setSeatNumber(seatNumber);
                seat.setCoach(coach);
                
                
                seat.setPrice(seatPrice);
                
                // Add the seat to the coach's seat list
                seats.add(seat);
            }
        }
        
        // Set the seats in the coach
        coach.setSeats(seats);
        seatRepository.saveAll(seats); // Save all seats to the database
        
        return coachRepository.save(coach);
    }
    
    

    
    public Coach updateCoach(Long id, Coach coachDetails) {
        return coachRepository.findById(id)
                .map(coach -> {
                    coach.setRouteId(coachDetails.getRouteId());
                    coach.setTrainId(coachDetails.getTrainId());

                    coach.setCoachClass(coachDetails.getCoachClass());
                    coach.setSeats(coachDetails.getSeats());
                    return coachRepository.save(coach);
                })
                .orElseThrow(() -> new RuntimeException("Coach not found with ID: " + id));
    }

    
    public void deleteAllCoaches() {
        seatRepository.deleteAll();
        coachRepository.deleteAll();
    }
    
    
    
    public void deleteCoachById(Long id) {
        Optional<Coach> coach = coachRepository.findById(id);
        if (coach.isPresent()) {
            List<Seat> seatList = coach.get().getSeats();
            seatRepository.deleteAll(seatList);
            coachRepository.deleteById(id);
        }
        else{
            throw new RuntimeException("Coach not found with ID: " + id);
        }
    }
    
    

    public List<Boolean> getUnavailableSeats(Long coachId) {

        Optional<Coach> coach = coachRepository.findById(coachId);
        if (coach.isPresent()) {
            return seatLockService.whichSeatsAreLocked(coach.get().getSeats());
        } else {
            throw new RuntimeException("Coach not found with ID: " + coachId);
        }
    }

    public List<Seat> getSeatsByCoachId(Long coachId) {

        Optional<Coach> coach = coachRepository.findById(coachId);
        if (coach.isEmpty()) {
            throw new RuntimeException("Coach not found with ID: " + coachId);
        }
        return seatRepository.findByCoach(coach.get());
    }
}
