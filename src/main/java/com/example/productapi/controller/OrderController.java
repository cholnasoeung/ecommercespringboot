package com.example.productapi.controller;

import com.example.productapi.dto.OrderResponse;
import com.example.productapi.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout() {
        return ResponseEntity.ok(orderService.checkout());
    }
}