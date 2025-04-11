package com.example.payment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.payment.dto.PaymentRequest;
import com.example.payment.dto.PaymentResponse;
import com.example.payment.dto.PaymentStatusRequest;
import com.example.payment.service.PaymentService;

@RestController
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest paymentRequest) {

        try {
            PaymentResponse paymentResponse = paymentService.createPayment(paymentRequest);

            if (paymentResponse != null) {

                return ResponseEntity.ok(paymentResponse);
            }

            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
        } catch (Exception e) {

            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/status")
    public ResponseEntity<PaymentResponse> getPaymentDetails(@RequestBody PaymentStatusRequest paymentStatusRequest) {

        PaymentResponse paymentResponse = paymentService.getPaymentDetails(paymentStatusRequest);

        if (paymentResponse == null) {

            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
        }

        return ResponseEntity.ok(paymentResponse);
    }

    @GetMapping
    public String helloThere() {
        return "hello";
    }

}
