package com.iteam.buget.core.auth.dto;

import com.iteam.buget.core.user.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private UserDTO user;
    private String accessToken;
}
