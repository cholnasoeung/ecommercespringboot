package com.example.productapi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}