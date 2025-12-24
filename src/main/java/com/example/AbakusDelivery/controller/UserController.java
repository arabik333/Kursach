package com.example.AbakusDelivery.controller;

import com.example.AbakusDelivery.dto.request.UserRequest;
import com.example.AbakusDelivery.dto.response.UserResponse;
import com.example.AbakusDelivery.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserService userService;

    // Все пользователи — только админ
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAll().stream()
                .map(UserResponse::new)
                .toList());
    }

    // Конкретный пользователь — только админ
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(new UserResponse(userService.getById(id)));
    }

    // Создание пользователя через API (опционально, обычно используется /auth/register)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserRequest request) {
        return ResponseEntity.ok(new UserResponse(userService.create(request)));
    }
}
