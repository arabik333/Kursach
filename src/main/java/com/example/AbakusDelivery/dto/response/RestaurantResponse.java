package com.example.AbakusDelivery.dto.response;

import com.example.AbakusDelivery.entity.Restaurant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantResponse {

    private Long id;
    private String name;
    private String address;
    private String phone;

    public RestaurantResponse(Restaurant restaurant) {
        id = restaurant.getId();
        name = restaurant.getName();
        address = restaurant.getAddress();
        phone = restaurant.getPhone();
    }
}
