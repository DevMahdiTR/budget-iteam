package com.iteam.buget.core.dto.response;

import lombok.*;

@Data @Builder
public class AuthResponse {
    private String accessToken;
    private String tokenType;
    private UserResponse user;
}
