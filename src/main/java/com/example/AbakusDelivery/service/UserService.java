package com.example.AbakusDelivery.service;

import com.example.AbakusDelivery.dto.request.UserRequest;
import com.example.AbakusDelivery.entity.Role;
import com.example.AbakusDelivery.entity.User;
import com.example.AbakusDelivery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
    }

    public User create(UserRequest request) {
        return createWithRole(request, Role.ROLE_USER);
    }

    public User createWithRole(UserRequest request, Role role) {
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .role(role)
                .build();

        return userRepository.save(user);
    }
}
