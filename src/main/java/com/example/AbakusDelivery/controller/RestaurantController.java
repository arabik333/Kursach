package com.example.AbakusDelivery.controller;

import com.example.AbakusDelivery.dto.request.RestaurantRequest;
import com.example.AbakusDelivery.dto.response.RestaurantResponse;
import com.example.AbakusDelivery.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/restaurants")
@CrossOrigin(origins = "http://localhost:3000")
public class RestaurantController {

    private final RestaurantService restaurantService;

    // Публичный список ресторанов
    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantService.getAll().stream()
                .map(RestaurantResponse::new)
                .toList());
    }

    // Публичная информация о конкретном ресторане
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurant(@PathVariable Long id) {
        return ResponseEntity.ok(new RestaurantResponse(restaurantService.getById(id)));
    }

    // Создание ресторана — только админ
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<RestaurantResponse> createRestaurant(@RequestBody @Valid RestaurantRequest request) {
        return ResponseEntity.ok(new RestaurantResponse(restaurantService.create(request)));
    }

    // Обновление ресторана — только админ
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<RestaurantResponse> updateRestaurant(@PathVariable Long id,
                                                               @RequestBody @Valid RestaurantRequest request) {
        return ResponseEntity.ok(new RestaurantResponse(restaurantService.update(id, request)));
    }

    // Удаление ресторана — только админ
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id) {
        restaurantService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
