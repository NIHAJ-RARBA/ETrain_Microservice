package com.example.payment.service;


import org.springframework.stereotype.Service;

import com.example.payment.dto.PaymentRequest;
import com.example.payment.dto.PaymentResponse;
import com.example.payment.dto.PaymentStatusRequest;
import com.example.payment.model.Payment;
import com.example.payment.repository.PaymentRepository;

@Service
public class PaymentService {
    
    private final PaymentRepository paymentRepository ;
    private final PaymentGateway paymentGateway;

    public PaymentService(PaymentRepository paymentRepository,PaymentGateway paymentGateway){
        this.paymentRepository = paymentRepository;
        this.paymentGateway = paymentGateway;
    }


    public PaymentResponse createPayment(PaymentRequest paymentRequest){

        
        PaymentResponse paymentResponse = paymentGateway.processPayment(paymentRequest);

        Payment payment = Payment.builder()
            .amount(paymentRequest.getAmount())
            .currency(paymentRequest.getCurrency())
            .userId(paymentRequest.getUserId())
            .transactionId(paymentResponse.getTransactionId())
            .status(paymentResponse.getStatus())
            .build();

        paymentRepository.save(payment);


        return paymentResponse;
    }

    public PaymentResponse getPaymentDetails(PaymentStatusRequest paymentStatusRequest){

        Payment payment = paymentRepository.findByTransactionId(paymentStatusRequest.getTransactionId())
                                           .orElse(null);

        if(payment==null){

            return null;
        }

        return PaymentResponse.builder()
                .transactionId(payment.getTransactionId())
                .processedAt(payment.getCreatedAt())
                .status(payment.getStatus())
                .message("Payment details")
                .build();

    }


}
