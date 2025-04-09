package com.Coach_Seat_BookingMS.Coach_Seat_Booking.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.enums.PassengerType;

@Entity
@Data
@NoArgsConstructor
public class Passengers {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;


    private PassengerType type = PassengerType.ADULT;

    
}

