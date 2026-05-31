package com.iteam.buget.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @NotBlank private String firstName;
    @NotBlank private String lastName;
}
