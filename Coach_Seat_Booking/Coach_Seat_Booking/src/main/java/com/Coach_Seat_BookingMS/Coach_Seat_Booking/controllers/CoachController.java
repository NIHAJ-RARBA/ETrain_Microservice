package com.Coach_Seat_BookingMS.Coach_Seat_Booking.controllers;

import com.Coach_Seat_BookingMS.Coach_Seat_Booking.dtos.CoachRequest;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.dtos.IdRequest;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.dtos.RouteClassRequest;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.models.Coach;

import com.Coach_Seat_BookingMS.Coach_Seat_Booking.services.CoachSeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/coach")
@RequiredArgsConstructor
public class CoachController 
{
    private final CoachSeatService coachSeatService;

    @GetMapping("/all")
    public ResponseEntity<List<Coach>> getAllCoaches() {
        List<Coach> coaches = coachSeatService.getAllCoaches();
        return new ResponseEntity<>(coaches, HttpStatus.OK);
    }

    @GetMapping("/byRouteId")
    public ResponseEntity<List<Coach>> getCoachesByRouteId(@RequestBody IdRequest idRequest) {
        Long routeId = idRequest.getId();
        List<Coach> coaches = coachSeatService.getCoachesByRouteId(routeId);
        return new ResponseEntity<>(coaches, HttpStatus.OK);
    }

    @GetMapping("/byID")
    public ResponseEntity<List<Coach>> getCoachesById(@RequestBody IdRequest idRequest) {
        Long id = idRequest.getId();
        Optional<Coach> coach = coachSeatService.getCoachById(id);
        if (coach.isPresent()) {
            return new ResponseEntity<>(List.of(coach.get()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/byRouteAndClass")
    public ResponseEntity<List<Coach>> getCoachesByRouteAndClass(@RequestBody RouteClassRequest routeClassRequest) {
        Long routeId = routeClassRequest.getRouteId();
        String coachClass = routeClassRequest.getCoachClass();
        // Validate the input parameters
        if (routeId == null || routeClassRequest.getCoachClass() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        
        List<Coach> coaches = coachSeatService.getCoachesByRouteAndClass(routeId, coachClass);
        return new ResponseEntity<>(coaches, HttpStatus.OK);
    }

    
    @PostMapping
    public ResponseEntity<Coach> addCoach(@RequestBody CoachRequest addCoachRequest) {
        // Validate the input parameters
        if (addCoachRequest.getRouteId() == null || addCoachRequest.getTrainId() == null || addCoachRequest.getCoachClass() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // Create a new Coach object from the request
        Coach coach = new Coach();
        coach.setRouteId(addCoachRequest.getRouteId());
        coach.setTrainId(addCoachRequest.getTrainId());
        coach.setCoachClass(addCoachRequest.getCoachClass());
        // coach.setTotalSeats(addCoachRequest.getTotalSeats()); 


        Coach createdCoach = coachSeatService.createCoach(coach);
        return new ResponseEntity<>(createdCoach, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Coach> updateCoach(@RequestBody CoachRequest updateCoachRequest) {
        

        Coach coach = coachSeatService.getCoachesByRouteAndClass(updateCoachRequest.getRouteId(), updateCoachRequest.getCoachClass()).get(0);

        if (coach == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Update the coach details

        coach.setRouteId(updateCoachRequest.getRouteId());
        coach.setTrainId(updateCoachRequest.getTrainId());
        coach.setCoachClass(updateCoachRequest.getCoachClass());
        // coach.setTotalSeats(updateCoachRequest.getTotalSeats());
        
        if (updateCoachRequest.getTotalSeats() == coach.getTotalSeats()) {
            return new ResponseEntity<>(coach, HttpStatus.OK);
        } else {

            coachSeatService.deleteCoachById(coach.getCoachId());
            
            return new ResponseEntity<>(coachSeatService.createCoach(coach), HttpStatus.OK);
        }
    }


    @DeleteMapping("byId")
    public ResponseEntity<Void> deleteCoachById(@RequestBody IdRequest idRequest) {
        Long id = idRequest.getId();
        Optional<Coach> coach = coachSeatService.getCoachById(id);
        if (coach.isPresent()) {
            coachSeatService.deleteCoachById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAllCoaches() {
        coachSeatService.deleteAllCoaches();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/byRouteId")
    public ResponseEntity<Void> deleteCoachesByRouteId(@RequestBody IdRequest idRequest) {
        Long routeId = idRequest.getId();
        List<Coach> coaches = coachSeatService.getCoachesByRouteId(routeId);
        if (coaches.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            for (Coach coach : coaches) {
                coachSeatService.deleteCoachById(coach.getCoachId());
            }
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @DeleteMapping("/byRouteAndClass")
    public ResponseEntity<Void> deleteCoachesByRouteAndClass(@RequestBody RouteClassRequest routeClassRequest) {
        Long routeId = routeClassRequest.getRouteId();
        String coachClass = routeClassRequest.getCoachClass();
        List<Coach> coaches = coachSeatService.getCoachesByRouteAndClass(routeId, coachClass);
        if (coaches.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            for (Coach coach : coaches) {
                coachSeatService.deleteCoachById(coach.getCoachId());
            }
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    
}
