package com.example.productapi.service;

import com.example.productapi.dto.AddToCartRequest;
import com.example.productapi.dto.CartResponse;
import com.example.productapi.dto.CartItemResponse;
import com.example.productapi.entity.*;
import com.example.productapi.exception.ResourceNotFoundException;
import com.example.productapi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // Helper method to get the currently logged-in user from JWT
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional
    public void addToCart(AddToCartRequest request) {
        User user = getCurrentUser();

        // 1. Find or create a cart for the user
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });

        // 2. Find the product
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // 3. Check stock
        if (product.getStockQuantity() < request.quantity()) {
            throw new RuntimeException("Not enough stock available for " + product.getName());
        }

        // 4. Check if item is already in cart
        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product).orElse(null);

        if (cartItem != null) {
            // If it exists, just increase the quantity
            cartItem.setQuantity(cartItem.getQuantity() + request.quantity());
        } else {
            // If not, create a new CartItem
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.quantity());
        }

        cartItemRepository.save(cartItem);
    }

    @Transactional(readOnly = true)
    public CartResponse getMyCart() {
        User user = getCurrentUser();
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart is empty or not found"));

        List<CartItemResponse> itemResponses = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        if (cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                BigDecimal subTotal = item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                totalPrice = totalPrice.add(subTotal);

                itemResponses.add(new CartItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getProduct().getPrice(),
                        item.getQuantity(),
                        subTotal
                ));
            }
        }

        return new CartResponse(cart.getId(), itemResponses, totalPrice);
    }
}