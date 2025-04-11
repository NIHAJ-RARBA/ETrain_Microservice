package com.example.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.payment.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment,Long>{

    Optional<Payment> findByTransactionId(String transactionId);
  
}