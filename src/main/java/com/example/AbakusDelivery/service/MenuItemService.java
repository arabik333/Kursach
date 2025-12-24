package com.example.AbakusDelivery.service;

import com.example.AbakusDelivery.dto.request.MenuItemRequest;
import com.example.AbakusDelivery.entity.MenuItem;
import com.example.AbakusDelivery.entity.Restaurant;
import com.example.AbakusDelivery.repository.MenuItemRepository;
import com.example.AbakusDelivery.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    public List<MenuItem> getByRestaurant(Long restaurantId) {
        return menuItemRepository.findByRestaurantId(restaurantId);
    }

    public MenuItem getById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found: " + id));
    }

    public MenuItem create(Long restaurantId, MenuItemRequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found: " + restaurantId));

        MenuItem item = MenuItem.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .restaurant(restaurant)
                .build();

        return menuItemRepository.save(item);
    }

    public MenuItem update(Long id, MenuItemRequest request) {
        MenuItem item = getById(id);
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setCategory(request.getCategory());
        return menuItemRepository.save(item);
    }

    public void delete(Long id) {
        menuItemRepository.deleteById(id);
    }
}
