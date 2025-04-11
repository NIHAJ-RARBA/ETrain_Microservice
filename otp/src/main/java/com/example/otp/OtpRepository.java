package com.example.otp;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByEmailAndOtpCode(String email, String otpCode);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM Otp o WHERE o.expirationTime <= ?1")
    void deleteExpiredOtp(LocalDateTime now);
}