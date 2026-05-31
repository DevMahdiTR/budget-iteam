package com.iteam.buget.core.mapper;

import com.iteam.buget.core.dto.response.UserResponse;
import com.iteam.buget.core.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(User user) {
        if (user == null) return null;
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().getRoleName().toString())
                .enabled(user.isEnabled())
                .accountValidated(user.isAccountValidated())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
