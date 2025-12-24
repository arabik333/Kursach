package com.example.AbakusDelivery.controller;

import com.example.AbakusDelivery.dto.request.LoginRequest;
import com.example.AbakusDelivery.dto.request.UserRequest;
import com.example.AbakusDelivery.dto.response.AuthResponse;
import com.example.AbakusDelivery.dto.response.UserResponse;
import com.example.AbakusDelivery.entity.Role;
import com.example.AbakusDelivery.entity.User;
import com.example.AbakusDelivery.security.JwtService;
import com.example.AbakusDelivery.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid UserRequest request) {
        User user = userService.createWithRole(request, Role.ROLE_USER);
        return ResponseEntity.ok(new UserResponse(user));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword());

        authenticationManager.authenticate(authToken);

        User user = userService.getByEmail(request.getEmail());
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .build();

        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(token));
    }
}
