package com.Coach_Seat_BookingMS.Coach_Seat_Booking.services;

import com.Coach_Seat_BookingMS.Coach_Seat_Booking.models.Seat;
import com.Coach_Seat_BookingMS.Coach_Seat_Booking.enums.SeatStatus;

import com.Coach_Seat_BookingMS.Coach_Seat_Booking.repositories.SeatRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SeatLockService {



    private static final Logger logger = LoggerFactory.getLogger(SeatLockService.class);

    // RedisTemplate is used to interact with Redis
    // It is assumed that Redis is already configured in the application context
    // You can use @Autowired to inject it if you have a Redis configuration class
    // or you can create a RedisConfig class to configure RedisTemplate bean
    // For example:
    // @Bean
    // public RedisTemplate<String, String> redisTemplate() {
    //     RedisTemplate<String, String> template = new RedisTemplate<>();
    //     template.setConnectionFactory(redisConnectionFactory());
    private final RedisTemplate<String, String> redisTemplate;
    private final SeatRepository seatRepository;

    @Value("${seat.lock.duration.seconds:600}")
    private long lockDuration;
    



    public boolean lockSeat(Long seatId, Long userId) {
        logger.info("Attempting to lock seat: {}", seatId);
        String lockKey = "seat_lock:" + seatId + ":" + userId; 
        try {
            Seat seat = seatRepository.findById(seatId)
                                    .orElseThrow(() -> new IllegalArgumentException("Seat not found"));
         
            if (seat.getStatus() == SeatStatus.UNAVAILABLE) {
                logger.warn("Seat is already unavailable: {}", seatId);
                return false;
            }


            Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, userId.toString(), lockDuration, TimeUnit.SECONDS);
            if (Boolean.TRUE.equals(success)) {
                logger.info("Successfully locked seat: {}", seatId);
                seat.setStatus(SeatStatus.UNAVAILABLE);
                // seatRepository.save(seat);
                return true;
            }
            logger.warn("Failed to lock seat: {} (already locked)", seatId);
            return false;
        } catch (Exception e) {
            logger.error("Error while locking seat: {}", seatId, e);
            throw new RuntimeException("Failed to lock seat: " + seatId, e);
        }
    }

    public boolean lockSeatsForUser(Long[] seatIds, Long userId) {

        for (Long seatId : seatIds) {
            if (!lockSeat(seatId, userId)) {
                return false;
            }
        }
        return true;
    }

    public boolean isSeatLocked(Long seatId, Long userId) {
        logger.info("Checking if seat is locked: {} for user: {}", seatId, userId);
        String lockKey = "seat_lock:" + seatId + ":" + userId;
    
        try {
            Boolean isLocked = redisTemplate.hasKey(lockKey);
            if (Boolean.TRUE.equals(isLocked)) {
                logger.info("Seat is locked by user {}: {}", userId, seatId);
                return true;
            } else {
                logger.info("Seat is not locked by user {}: {}", userId, seatId);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error while checking seat lock for seat {} and user {}: {}", seatId, userId, e.getMessage());
            return false;
        }
    }
    

    // public List<Boolean> whichSeatsAreLocked(List<Seat> seats) {
    //     List<Boolean> locked = new ArrayList<>();
    //     for (Seat seat : seats) {
    //         locked.add(isSeatLocked(seat.getSeatId()));
    //     }
    //     return locked;
    // }

    public void unlockSeat(Long seatId, Long userId) {
        logger.info("Unlocking seat: {} for user: {}", seatId, userId);
        logger.info("Unlocking seat: {}", seatId);
        String lockKey = "seat_lock:" + seatId + ":" + userId; // Use the same lock key format as in lockSeat method
        try {
            redisTemplate.delete(lockKey);

            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new IllegalArgumentException("Seat not found"));
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setTicket(null); // Remove ticket reference from seat
            seatRepository.save(seat);
        } catch (Exception e) {
            logger.error("Error while unlocking seat: {}", seatId, e);
            throw new RuntimeException("Failed to unlock seat: " + seatId, e);
        }
    }


    public void unlockSeats(Long[] seatIds, Long userId) {
        System.out.println("Unlocking seats: " + seatIds);

        for (Long seatId : seatIds) {
            unlockSeat(seatId, userId);

        }
    }

    public void unlockSeats(List<Seat> seats, Long userId) {
        System.out.println("Unlocking seats: " + seats);
        for (Seat seat : seats) {
            unlockSeat(seat.getSeatId(), userId);
        }
    }



    

}