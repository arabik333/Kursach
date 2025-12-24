package com.example.AbakusDelivery.dto.response;

import com.example.AbakusDelivery.entity.Order;
import com.example.AbakusDelivery.entity.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
public class OrderResponse {

    private Long id;
    private Long userId;
    private String userFullName;
    private OrderStatus status;
    private String comment;
    private BigDecimal totalPrice;
    private OffsetDateTime createdAt;
    private List<OrderItemResponse> items;

    public OrderResponse(Order order) {
        id = order.getId();
        userId = order.getUser().getId();
        userFullName = order.getUser().getFullName();
        status = order.getStatus();
        comment = order.getComment();
        totalPrice = order.getTotalPrice();
        createdAt = order.getCreatedAt();
        items = order.getItems().stream()
                .map(OrderItemResponse::new)
                .toList();
    }
}
