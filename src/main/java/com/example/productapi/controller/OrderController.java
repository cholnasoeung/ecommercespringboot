package com.example.productapi.controller;

import com.example.productapi.dto.OrderRequest;
import com.example.productapi.dto.OrderResponse;
import com.example.productapi.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(@RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.checkout(request));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrderHistory() {
        return ResponseEntity.ok(orderService.getUserOrderHistory());
    }
}