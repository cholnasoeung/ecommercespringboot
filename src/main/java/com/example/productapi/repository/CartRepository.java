package com.example.productapi.repository;

import com.example.productapi.entity.Cart;
import com.example.productapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}