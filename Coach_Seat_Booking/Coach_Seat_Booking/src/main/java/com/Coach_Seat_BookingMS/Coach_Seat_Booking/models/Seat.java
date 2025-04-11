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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.Coach_Seat_BookingMS.Coach_Seat_Booking.enums.SeatStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "ticket")
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

    // @ManyToOne
    // // @JoinColumn(name = "id", nullable = false)
    // private Coach coach; // Relationship with Coach entity

    @Column(name = "coach_id", nullable = false)
    private Long coachId;
        
    @ManyToOne
    @JoinColumn(name = "ticket", unique = true)
    @JsonBackReference
    private Ticket ticket;





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
        return coachId;
    }

    public void setCoachId(Long coachId) {
        this.coachId = coachId;
    }


    
}
