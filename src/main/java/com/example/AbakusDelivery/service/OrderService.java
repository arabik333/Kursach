package com.example.AbakusDelivery.service;

import com.example.AbakusDelivery.dto.request.CreateOrderRequest;
import com.example.AbakusDelivery.dto.request.OrderItemRequest;
import com.example.AbakusDelivery.dto.response.OrderResponse;
import com.example.AbakusDelivery.entity.MenuItem;
import com.example.AbakusDelivery.entity.Order;
import com.example.AbakusDelivery.entity.OrderItem;
import com.example.AbakusDelivery.entity.OrderStatus;
import com.example.AbakusDelivery.entity.User;
import com.example.AbakusDelivery.repository.MenuItemRepository;
import com.example.AbakusDelivery.repository.OrderRepository;
import com.example.AbakusDelivery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final UserRepository userRepository;

    /**
     * Создание заказа от текущего аутентифицированного пользователя.
     */
    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        User user = getCurrentUser();

        Order order = new Order();
        order.setUser(user);
        order.setComment(request.getComment());
        order.setStatus(OrderStatus.NEW);
        order.setCreatedAt(OffsetDateTime.now());

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Menu item not found: " + itemRequest.getMenuItemId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(menuItem.getPrice());

            BigDecimal lineTotal = menuItem.getPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            total = total.add(lineTotal);

            items.add(orderItem);
        }

        order.setItems(items);
        order.setTotalPrice(total);

        Order saved = orderRepository.save(order);
        return new OrderResponse(saved);
    }

    /**
     * Один заказ по id (для админа или последующей доработки с проверкой владельца).
     */
    public OrderResponse getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
        return new OrderResponse(order);
    }

    /**
     * Все заказы (для админа).
     */
    public List<OrderResponse> getAll() {
        return orderRepository.findAll().stream()
                .map(OrderResponse::new)
                .toList();
    }

    /**
     * Заказы конкретного пользователя по его id (для админа).
     */
    public List<OrderResponse> getByUser(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(OrderResponse::new)
                .toList();
    }

    /**
     * Заказы текущего залогиненного пользователя (история заказов в личном кабинете).
     */
    public List<OrderResponse> getMyOrders() {
        User user = getCurrentUser();
        return orderRepository.findByUserId(user.getId()).stream()
                .map(OrderResponse::new)
                .toList();
    }

    /**
     * Заказы по статусу (для админки: новые, в пути, доставлены и т.п.).
     */
    public List<OrderResponse> getByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
                .map(OrderResponse::new)
                .toList();
    }

    /**
     * Изменение статуса заказа (для админа).
     */
    @Transactional
    public OrderResponse updateStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
        order.setStatus(status);
        Order saved = orderRepository.save(order);
        return new OrderResponse(saved);
    }

    /**
     * Вспомогательный метод: достать текущего пользователя из SecurityContext по его email,
     * который хранится как username в UserDetails, что соответствует рекомендациям по JWT‑аутентификации [web:61][web:74].
     */
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }

        String email;
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        } else {
            email = principal.toString();
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
    }
}
