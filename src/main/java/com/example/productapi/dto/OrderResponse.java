package com.example.productapi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        Long orderId,
        LocalDateTime orderDate,
        BigDecimal totalAmount,
        String status,
        String message
) {}