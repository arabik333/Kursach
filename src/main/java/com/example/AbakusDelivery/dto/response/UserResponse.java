package com.example.AbakusDelivery.dto.response;

import com.example.AbakusDelivery.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {

    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private String role;

    public UserResponse(User user) {
        id = user.getId();
        email = user.getEmail();
        fullName = user.getFullName();
        phone = user.getPhone();
        address = user.getAddress();
        role = user.getRole() != null ? user.getRole().name() : null;
    }
}
