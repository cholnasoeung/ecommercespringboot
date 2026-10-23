package com.example.productapi.dto;
import java.math.BigDecimal;
public record OrderItemDto(String productName, Integer quantity, BigDecimal price) {}