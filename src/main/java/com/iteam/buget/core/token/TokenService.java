package com.iteam.buget.core.token;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {


    private final TokenRepository tokenRepository;


    public Token findByToken(final String token) {
        return tokenRepository.findByToken(token).orElseThrow(
                () -> new IllegalStateException("Token not found")
        );
    }
}
