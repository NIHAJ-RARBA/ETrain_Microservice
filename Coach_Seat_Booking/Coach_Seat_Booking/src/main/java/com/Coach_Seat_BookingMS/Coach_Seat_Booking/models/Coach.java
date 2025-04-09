package com.Coach_Seat_BookingMS.Coach_Seat_Booking.models;

import java.util.List;

import com.Coach_Seat_BookingMS.Coach_Seat_Booking.enums.CoachClass;

import jakarta.persistence.CascadeType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Coach {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long routeId; // Foreign key to Route (stored as plain Long)

    private Long trainId; // Foreign key to Train (stored as plain Long)

    @OneToMany(mappedBy = "coach", cascade = CascadeType.ALL)
    private List<Seat> seats;
    
    

    // private int totalSeats; // Total number of seats in the coach

    @Enumerated(EnumType.STRING)
    private CoachClass coachClass;


    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    public void setCoachClass(CoachClass coachClass) {
        this.coachClass = coachClass;
    }

    public void setCoachClass(String coachClass) {
        this.coachClass = CoachClass.fromString(coachClass);
    }

    public Double getSeatPrice() {
        return coachClass != null ? coachClass.getBasePrice() : 0.0;
    }

    public int getTotalSeats() {
        return seats != null ? seats.size() : 0; // Return the size of the seats list
    }
    public Long getCoachId() {
        return id;
    }
    
}