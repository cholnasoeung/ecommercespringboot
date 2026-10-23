package com.example.productapi.service;

import com.example.productapi.dto.AdminOrderResponse;
import com.example.productapi.dto.OrderItemDto;
import com.example.productapi.dto.OrderRequest;
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
    public OrderResponse checkout(OrderRequest request) {
        User user = getCurrentUser();
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        // Save shipping details
        order.setShippingAddress(request.shippingAddress());
        order.setPhoneNumber(request.phoneNumber());
        order.setPaymentMethod(request.paymentMethod());

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(product.getPrice());
            orderItems.add(orderItem);

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        order.setTotalAmount(totalAmount);
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        // Properly clear the cart so the frontend sees it's empty
        cart.getItems().clear();
        cartRepository.save(cart);

        return new OrderResponse(
                savedOrder.getId(),
                savedOrder.getOrderDate(),
                savedOrder.getTotalAmount(),
                savedOrder.getStatus().name(),
                "Checkout successful! Order placed."
        );
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrderHistory() {
        User user = getCurrentUser();
        List<Order> orders = orderRepository.findByUserOrderByOrderDateDesc(user);

        return orders.stream().map(order -> new OrderResponse(
                order.getId(),
                order.getOrderDate(),
                order.getTotalAmount(),
                order.getStatus().name(),
                "Order retrieved successfully"
        )).toList();
    }

    @Transactional(readOnly = true)
    public List<AdminOrderResponse> getAllOrdersForAdmin() {
        return orderRepository.findAll().stream().map(order -> new AdminOrderResponse(
                order.getId(),
                order.getUser().getEmail(),
                order.getOrderDate(),
                order.getTotalAmount(),
                order.getStatus().name(),
                order.getShippingAddress(),
                order.getPhoneNumber(),
                order.getPaymentMethod(),
                order.getItems().stream().map(item -> new OrderItemDto(
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPriceAtPurchase()
                )).toList()
        )).toList();
    }

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
                saved.getStatus().name(),
                saved.getShippingAddress(),
                saved.getPhoneNumber(),
                saved.getPaymentMethod(),
                saved.getItems().stream().map(item -> new OrderItemDto(
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPriceAtPurchase()
                )).toList()
        );
    }
}