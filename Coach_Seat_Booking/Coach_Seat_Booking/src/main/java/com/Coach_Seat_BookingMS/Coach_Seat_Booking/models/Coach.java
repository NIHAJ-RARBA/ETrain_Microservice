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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "seats")
public class Coach {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long routeId; // Foreign key to Route (stored as plain Long)

    private Long trainId; // Foreign key to Train (stored as plain Long)

    @OneToMany(mappedBy = "coachId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats;
    
    

    private int totalSeats; // Total number of seats in the coach

    @Enumerated(EnumType.STRING)
    private CoachClass coachClass;



    public void setSeats(List<Seat> seats) {
        if (seats == null) {
            throw new IllegalArgumentException("Seats list cannot be null");
        }
        this.seats = seats;
        this.totalSeats = seats.size();
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
        
            
    }
    
    public int getTotalSeats() {
        return totalSeats;
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
    public Long getCoachId() {
        return id;
    }
    
}