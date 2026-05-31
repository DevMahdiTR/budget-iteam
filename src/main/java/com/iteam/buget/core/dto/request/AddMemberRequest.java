package com.iteam.buget.core.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AddMemberRequest {
    @Email @NotBlank private String email;
}
