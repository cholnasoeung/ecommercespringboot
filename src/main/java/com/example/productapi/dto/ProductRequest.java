package com.example.productapi.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "name is required")
        @Size(max = 150)
        String name,

        String description,

        @NotNull(message = "price is required")
        @DecimalMin(value = "0.01", message = "price must be > 0")
        BigDecimal price,

        @NotNull(message = "quantity is required")
        @PositiveOrZero
        Integer quantity
) {}