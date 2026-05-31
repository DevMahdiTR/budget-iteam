package com.iteam.buget.core.auth;

import com.iteam.buget.core.dto.request.LoginRequest;
import com.iteam.buget.core.dto.request.RegisterRequest;
import com.iteam.buget.core.dto.response.AuthResponse;
import com.iteam.buget.core.mapper.UserMapper;
import com.iteam.buget.core.role.Role;
import com.iteam.buget.core.role.RoleName;
import com.iteam.buget.core.role.RoleRepository;
import com.iteam.buget.core.token.Token;
import com.iteam.buget.core.token.TokenRepository;
import com.iteam.buget.core.user.User;
import com.iteam.buget.core.user.UserRepository;
import com.iteam.buget.exception.AppException;
import com.iteam.buget.security.jwt.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException("Email already in use", HttpStatus.CONFLICT);
        }
        Role userRole = roleRepository.findByRoleName(RoleName.ROLE_USER)
                .orElseThrow(() -> new AppException("Default role not found", HttpStatus.INTERNAL_SERVER_ERROR));

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .accountValidated(false) // admin must validate
                .deletionRequested(false)
                .role(userRole)
                .build();
        userRepository.save(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        if (!user.isAccountValidated()) {
            throw new AppException("Account not yet validated by admin", HttpStatus.FORBIDDEN);
        }

        revokeAllUserTokens(user);
        String jwt = jwtService.generateToken(user);
        saveToken(user, jwt);

        return AuthResponse.builder()
                .accessToken(jwt)
                .tokenType("Bearer")
                .user(userMapper.toResponse(user))
                .build();
    }

    @Transactional
    public void logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return;
        String jwt = authHeader.substring(7);
        tokenRepository.findByToken(jwt).ifPresent(t -> {
            t.setRevoked(true);
            t.setExpired(true);
            tokenRepository.save(t);
        });
    }

    private void saveToken(User user, String jwt) {
        Token token = Token.builder()
                .token(jwt)
                .user(user)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        List<Token> tokens = tokenRepository.findAllValidTokensByUser(user.getId());
        tokens.forEach(t -> { t.setRevoked(true); t.setExpired(true); });
        tokenRepository.saveAll(tokens);
    }
}
