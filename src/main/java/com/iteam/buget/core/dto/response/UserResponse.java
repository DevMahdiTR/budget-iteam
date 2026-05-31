package com.iteam.buget.core.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder
public class UserResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private boolean enabled;
    private boolean accountValidated;
    private LocalDateTime createdAt;
}
