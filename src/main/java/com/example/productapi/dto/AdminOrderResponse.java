package com.example.productapi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AdminOrderResponse(
        Long orderId,
        String customerEmail,
        LocalDateTime orderDate,
        BigDecimal totalAmount,
        String status
) {}