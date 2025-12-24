package com.example.AbakusDelivery.controller;

import com.example.AbakusDelivery.dto.request.CreateOrderRequest;
import com.example.AbakusDelivery.dto.response.OrderResponse;
import com.example.AbakusDelivery.entity.OrderStatus;
import com.example.AbakusDelivery.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    private final OrderService orderService;

    // Создать заказ (только авторизованный пользователь)
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.create(request));
    }

    // Свои заказы (личный кабинет пользователя)
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/me")
    public ResponseEntity<List<OrderResponse>> getMyOrders() {
        return ResponseEntity.ok(orderService.getMyOrders());
    }

    // Все заказы (для админа)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAll());
    }

    // Один заказ по id (для админа)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    // Заказы конкретного пользователя по id (для админа)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getByUser(userId));
    }

    // Изменить статус заказа (для админа)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Long id,
                                                      @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }
}
