package com.example.payment.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.example.payment.dto.PaymentRequest;
import com.example.payment.dto.PaymentResponse;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;

@Component
@Profile("mock")
@Slf4j
public class MockPaymentGateway implements PaymentGateway {

    private final Map<String, String> transactions = new ConcurrentHashMap<>();
    // private final Random random = new Random();

    @Value("${payment.mock.delay:500}")
    private int processingDelay;


    @Override
    public PaymentResponse processPayment(PaymentRequest request) {

        simulateProcessingDelay();

        String transactionId = "MOCK-" + UUID.randomUUID();
        String status = determineMockStatus(request.getCardNumber());

        transactions.put(transactionId, status);

        log.info("Mock payment processed: {}", transactionId);
        
        return new PaymentResponse(
                transactionId,
                status,
                Instant.now(),
                "Mock payment processed successfully");
    }

    private String determineMockStatus(String cardNumber) {
        // return switch (cardNumber.substring(0, 4)) {
        // case "4242" -> "SUCCESS;"
        // case "4000" -> "FAILED";
        // case "3782" -> "PENDING";
        // default -> random.nextBoolean() ? "SUCCESS" : "FAILED";
        // };

        String firstDigits = cardNumber.substring(0, 4);

        if (firstDigits.equals("4242")) {
            return "SUCCESS";
        } else if (firstDigits.equals("4000")) {
            return "FAILED";
        } else if (firstDigits.equals("3782")) {
            return "PENDING";
        } else {
            return null;
        }
    }

    private void simulateProcessingDelay() {
        try {
            Thread.sleep(processingDelay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // @Override
    // public PaymentResponse refundPayment(String transactionId) {
    //     // Similar implementation for refunds
    // }

    @Override
    public String checkStatus(String transactionId) {
        return transactions.getOrDefault(transactionId, "Failed");
    }
}