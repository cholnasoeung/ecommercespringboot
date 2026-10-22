package com.example.productapi.service;

import com.example.productapi.dto.OrderResponse;
import com.example.productapi.entity.*;
import com.example.productapi.exception.ResourceNotFoundException;
import com.example.productapi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.example.productapi.dto.AdminOrderResponse;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional
    public OrderResponse checkout() {
        User user = getCurrentUser();
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // 1. Create the Order
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        // 2. Process each item in the cart
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            // Check stock
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            // Deduct stock
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            // Create OrderItem (Historical record)
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(product.getPrice());
            orderItems.add(orderItem);

            // Add to total
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        order.setTotalAmount(totalAmount);
        order.setItems(orderItems);

        // 3. Save Order (this cascades to save OrderItems)
        Order savedOrder = orderRepository.save(order);

        // 4. Clear the Cart
        cartItemRepository.deleteAll(cart.getItems());

        return new OrderResponse(
                savedOrder.getId(),
                savedOrder.getOrderDate(),
                savedOrder.getTotalAmount(),
                savedOrder.getStatus().name(),
                "Checkout successful! Order placed."
        );
    }

    // 1. Get ALL orders in the system (Admin only)
    public List<AdminOrderResponse> getAllOrdersForAdmin() {
        return orderRepository.findAll().stream().map(order -> new AdminOrderResponse(
                order.getId(),
                order.getUser().getEmail(), // See who bought it
                order.getOrderDate(),
                order.getTotalAmount(),
                order.getStatus().name()
        )).toList();
    }

    // 2. Update order status
    @Transactional
    public AdminOrderResponse updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        try {
            order.setStatus(OrderStatus.valueOf(newStatus.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status. Use PENDING, SHIPPED, DELIVERED, or CANCELLED.");
        }

        Order saved = orderRepository.save(order);

        return new AdminOrderResponse(
                saved.getId(),
                saved.getUser().getEmail(),
                saved.getOrderDate(),
                saved.getTotalAmount(),
                saved.getStatus().name()
        );
    }
}