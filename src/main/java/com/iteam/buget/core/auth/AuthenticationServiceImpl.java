package com.iteam.buget.core.auth;


import com.iteam.buget.core.auth.dto.RegisterRequest;
import com.iteam.buget.core.role.Role;
import com.iteam.buget.core.role.RoleName;
import com.iteam.buget.core.token.TokenService;
import com.iteam.buget.core.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl {

    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;



}
