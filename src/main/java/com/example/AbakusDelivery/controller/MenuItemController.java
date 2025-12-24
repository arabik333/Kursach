package com.example.AbakusDelivery.controller;

import com.example.AbakusDelivery.dto.request.MenuItemRequest;
import com.example.AbakusDelivery.dto.response.MenuItemResponse;
import com.example.AbakusDelivery.service.MenuItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
@CrossOrigin(origins = "http://localhost:3000")
public class MenuItemController {

    private final MenuItemService menuItemService;

    // Публичное меню конкретного ресторана
    @GetMapping("/api/restaurants/{restaurantId}/menu-items")
    public ResponseEntity<List<MenuItemResponse>> getMenuByRestaurant(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(menuItemService.getByRestaurant(restaurantId).stream()
                .map(MenuItemResponse::new)
                .toList());
    }

    // Добавить блюдо в меню ресторана — только админ
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/api/restaurants/{restaurantId}/menu-items")
    public ResponseEntity<MenuItemResponse> createMenuItem(@PathVariable Long restaurantId,
                                                           @RequestBody @Valid MenuItemRequest request) {
        return ResponseEntity.ok(new MenuItemResponse(menuItemService.create(restaurantId, request)));
    }

    // Обновить существующее блюдо — только админ
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/api/menu-items/{id}")
    public ResponseEntity<MenuItemResponse> updateMenuItem(@PathVariable Long id,
                                                           @RequestBody @Valid MenuItemRequest request) {
        return ResponseEntity.ok(new MenuItemResponse(menuItemService.update(id, request)));
    }

    // Удалить блюдо — только админ
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/api/menu-items/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        menuItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
