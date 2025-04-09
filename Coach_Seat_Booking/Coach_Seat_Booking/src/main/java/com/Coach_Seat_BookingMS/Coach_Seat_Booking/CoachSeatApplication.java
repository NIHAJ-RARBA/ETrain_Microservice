package com.Coach_Seat_BookingMS.Coach_Seat_Booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
@EntityScan("com.Coach_Seat_BookingMS.Coach_Seat_Booking.models")

@EnableJpaRepositories(basePackages = "com.Coach_Seat_BookingMS.Coach_Seat_Booking.repositories")


@SpringBootApplication
@EnableScheduling
public class CoachSeatApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoachSeatApplication.class, args);
	}

}
