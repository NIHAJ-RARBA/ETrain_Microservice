package com.example.user.dto;

import lombok.Data;

@Data
public class OtpValidationRequest {
    private String email;
    private String otp;
}