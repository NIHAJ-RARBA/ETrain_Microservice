package com.example.payment.service;

import com.example.payment.dto.PaymentRequest;
import com.example.payment.dto.PaymentResponse;

public interface PaymentGateway {
    PaymentResponse processPayment(PaymentRequest request);
    //PaymentResponse refundPayment(String transactionId);
    String checkStatus(String transactionId);
}
