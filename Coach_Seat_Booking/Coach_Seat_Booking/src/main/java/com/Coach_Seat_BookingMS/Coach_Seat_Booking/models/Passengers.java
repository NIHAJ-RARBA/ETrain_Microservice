package com.Coach_Seat_BookingMS.Coach_Seat_Booking.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


import com.Coach_Seat_BookingMS.Coach_Seat_Booking.enums.PassengerType;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Data
@NoArgsConstructor
public class Passengers {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long passengerId;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "ticket", nullable = false)
    @JsonBackReference
    
    private Ticket ticket;

    private PassengerType type = PassengerType.ADULT;

    
}

