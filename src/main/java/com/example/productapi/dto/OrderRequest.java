package com.example.productapi.dto;

public record OrderRequest(
        String fullName,
        String phoneNumber,
        String shippingAddress,
        String paymentMethod
) {}