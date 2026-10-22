package com.example.productapi.controller;

import com.example.productapi.dto.AddToCartRequest;
import com.example.productapi.dto.CartResponse;
import com.example.productapi.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@Valid @RequestBody AddToCartRequest request) {
        cartService.addToCart(request);
        return ResponseEntity.ok("Item added to cart successfully");
    }

    @GetMapping
    public ResponseEntity<CartResponse> getMyCart() {
        return ResponseEntity.ok(cartService.getMyCart());
    }
}