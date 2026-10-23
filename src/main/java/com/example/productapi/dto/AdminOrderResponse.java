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
        String shippingAddress,
        String phoneNumber,
        String paymentMethod,
        List<OrderItemDto> items
) {}