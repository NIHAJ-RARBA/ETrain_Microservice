package com.example.user.dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class OtpGenerationRequest {
    private String email;

    public OtpGenerationRequest(String email){
        this.email = email;
    }
}