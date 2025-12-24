package com.example.AbakusDelivery.service;

import com.example.AbakusDelivery.dto.request.RestaurantRequest;
import com.example.AbakusDelivery.entity.Restaurant;
import com.example.AbakusDelivery.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public List<Restaurant> getAll() {
        return restaurantRepository.findAll();
    }

    public Restaurant getById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found: " + id));
    }

    public Restaurant create(RestaurantRequest request) {
        Restaurant restaurant = Restaurant.builder()
                .name(request.getName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .build();
        return restaurantRepository.save(restaurant);
    }

    public Restaurant update(Long id, RestaurantRequest request) {
        Restaurant restaurant = getById(id);
        restaurant.setName(request.getName());
        restaurant.setAddress(request.getAddress());
        restaurant.setPhone(request.getPhone());
        return restaurantRepository.save(restaurant);
    }

    public void delete(Long id) {
        restaurantRepository.deleteById(id);
    }
}
