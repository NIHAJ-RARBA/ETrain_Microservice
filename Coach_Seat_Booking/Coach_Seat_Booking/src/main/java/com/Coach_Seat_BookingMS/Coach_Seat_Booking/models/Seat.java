package com.Coach_Seat_BookingMS.Coach_Seat_Booking.models;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.Coach_Seat_BookingMS.Coach_Seat_Booking.enums.SeatStatus;

@Entity
@Data
@NoArgsConstructor
public class Seat {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seatId;

    @Column(nullable = false)
    private String seatNumber;

    @Column(nullable = false)
    private String bookingType;

    @Column(nullable = false)
    private double price;

    @ManyToOne
    @JoinColumn(name = "id", nullable = false)
    private Coach coach; // Relationship with Coach entity




    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status; // Available or Unavailable

    public void setStatus(SeatStatus status) {
        this.status = status;  
    }

    public SeatStatus getStatus() {
        return status;
    }

    public Long getCoachId() {
        return coach != null ? coach.getId() : null; // Return the ID of the associated Coach
    }


    
}
