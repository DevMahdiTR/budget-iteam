package com.iteam.buget.core.auth.dto;

import com.iteam.buget.config.LCRConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginRequest {

    @Email
    private String email;

    @Pattern(regexp = LCRConstants.PASSWORD_PATTERN)
    private String password;
}
