package com.example.orders.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record OrderRequest(@NotNull @Positive Long userId, @NotBlank @Size(max = 500) String description,
                           @NotNull @DecimalMin(value = "0.01") @Digits(integer = 10, fraction = 2) BigDecimal amount,
                           @Pattern(regexp = "NEW|PROCESSING|COMPLETED|CANCELLED") String status) {
}
