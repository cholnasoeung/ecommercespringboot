package com.example.productapi.controller;

import com.example.productapi.dto.AdminOrderResponse;
import com.example.productapi.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<AdminOrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrdersForAdmin());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AdminOrderResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }
}