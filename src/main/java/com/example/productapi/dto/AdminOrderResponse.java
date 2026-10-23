package com.example.productapi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AdminOrderResponse(
        Long orderId,
        String customerEmail,
        LocalDateTime orderDate,
        BigDecimal totalAmount,
        String status,
        List<OrderItemDto> items // <--- ADD THIS
) {}