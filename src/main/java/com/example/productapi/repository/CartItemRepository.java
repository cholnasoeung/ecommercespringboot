package com.example.productapi.repository;

import com.example.productapi.entity.CartItem;
import com.example.productapi.entity.Cart;
import com.example.productapi.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}