package com.Coach_Seat_BookingMS.Coach_Seat_Booking.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private boolean isPaid = false;

    @OneToMany
    private List<Seat> seats;

    @ManyToOne
    @JoinColumn(name = "id", nullable = false)
    private Coach coach;

    private double totalAmount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany
    private List<Passengers> passengers;

    // public Ticket(Long userId, List<Seat> seats) {
    //     this.userId = userId;
    //     this.seats = seats;
    //     this.totalAmount = calculateTotalAmount();
    //     this.createdAt = LocalDateTime.now();
    // }


    public Ticket(Long userId, Coach coach, List<Seat> seats, List<Passengers> passengers) 
    {
        this.userId = userId;
        this.seats = seats;
        this.coach = coach;
        this.totalAmount = calculateTotalAmount();
        this.createdAt = LocalDateTime.now();
    }

    private double calculateTotalAmount() {
        double total = 0.0;
        for (Passengers passenger : passengers) {
            total += passenger.getType().getPrice();
        }

        for (Seat seat : seats) {
            total += seat.getPrice();
        }

        return total;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
        this.totalAmount = calculateTotalAmount();
    }

    public void setPassengers(List<Passengers> passengers) {
        this.passengers = passengers;
        this.totalAmount = calculateTotalAmount();
    }

    // public List<Passengers> getPassengers() {
    //     return passengers;
    // }

}