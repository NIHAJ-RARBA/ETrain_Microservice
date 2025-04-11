package com.example.otp;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.otp.dto.OtpRequest;


@RestController
public class OtpController {

    private final OtpService otpService;

    public OtpController(OtpService otpService){
        this.otpService = otpService;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateOtp(@RequestBody OtpRequest request) {
        otpService.generateOtp(request.getEmail());
        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateOtp(@RequestBody Map<String, String> request) {
        boolean isValid = otpService.validateOtp(request.get("email"), request.get("otp"));
        return ResponseEntity.ok(isValid);
    }
}