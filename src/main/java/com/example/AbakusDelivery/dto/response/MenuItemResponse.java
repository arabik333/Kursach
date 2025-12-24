package com.example.AbakusDelivery.dto.response;

import com.example.AbakusDelivery.entity.MenuItem;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MenuItemResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private Long restaurantId;

    public MenuItemResponse(MenuItem item) {
        id = item.getId();
        name = item.getName();
        description = item.getDescription();
        price = item.getPrice();
        category = item.getCategory();
        restaurantId = item.getRestaurant().getId();
    }
}
