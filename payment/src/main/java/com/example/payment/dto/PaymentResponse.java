package com.example.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.Instant;

@Data
@AllArgsConstructor
@Getter
@Builder
public class PaymentResponse {
    private String transactionId;
    private String status;
    private Instant processedAt;
    private String message;
}
