package com.Coach_Seat_BookingMS.Coach_Seat_Booking.repositories;

import com.Coach_Seat_BookingMS.Coach_Seat_Booking.enums.CoachClass;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.models.Coach;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface CoachRepository extends JpaRepository<Coach, Long> {
    // Custom query methods can be defined here if needed
    
    
    List<Coach> findByCoachClass(CoachClass coachClass);

    // To find coaches by route ID
    List<Coach> findByRouteId(Long routeId);

    // To find coaches by route and class
    List<Coach> findByRouteIdAndCoachClass(Long routeId, CoachClass coachClass);
}