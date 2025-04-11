package com.example.otp;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {

    private final OtpRepository otpRepository;
    private final JavaMailSender mailSender;
    private final Random random = new Random();

    public OtpService(OtpRepository otpRepository, JavaMailSender mailSender) {
        this.otpRepository = otpRepository;
        this.mailSender = mailSender;
    }

    public String generateOtp(String email) {
        String otp = String.format("%06d", random.nextInt(999999));
        Otp otpEntry = new Otp();
        otpEntry.setEmail(email);
        otpEntry.setOtpCode(otp);
        otpEntry.setCreatedTime(LocalDateTime.now());
        otpEntry.setExpirationTime(LocalDateTime.now().plusMinutes(3));
        otpRepository.save(otpEntry);
        
        // Send email (simplified for example)
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is: " + otp);
        mailSender.send(message);
        
        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        Optional<Otp> otpEntry = otpRepository.findByEmailAndOtpCode(email, otp);
        if (otpEntry.isPresent() && LocalDateTime.now().isBefore(otpEntry.get().getExpirationTime())) {
            otpRepository.delete(otpEntry.get());
            return true;
        }
        return false;
    }

    @Scheduled(fixedRate = 180000) // Clean every 3 minutes
    public void cleanExpiredOtps() {
        otpRepository.deleteExpiredOtp(LocalDateTime.now());
    }
}