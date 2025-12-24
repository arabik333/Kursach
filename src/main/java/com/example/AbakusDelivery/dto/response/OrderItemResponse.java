package com.example.AbakusDelivery.dto.response;

import com.example.AbakusDelivery.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemResponse {

    private Long id;
    private Long menuItemId;
    private String menuItemName;
    private int quantity;
    private BigDecimal price;
    private BigDecimal lineTotal;

    public OrderItemResponse(OrderItem item) {
        id = item.getId();
        menuItemId = item.getMenuItem().getId();
        menuItemName = item.getMenuItem().getName();
        quantity = item.getQuantity();
        price = item.getPrice();
        lineTotal = price.multiply(BigDecimal.valueOf(quantity));
    }
}
