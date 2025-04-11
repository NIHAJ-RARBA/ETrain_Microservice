package com.example.payment.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class PaymentRequest {

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotBlank
    private String currency;

    @NotBlank
    @Pattern(regexp = "^\\d{16}$")
    private String cardNumber;

    @NotBlank
    @Pattern(regexp = "^(0[1-9]|1[0-2])\\/?([0-9]{2})$")
    private String expiry;

    @NotBlank
    @Size(min = 3, max = 4)
    private String cvv;

    @NotBlank
    private String userId;
}

