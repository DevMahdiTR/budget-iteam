package com.iteam.buget.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);         // 401
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Structured JSON error body
        Map<String, Object> errorBody = new LinkedHashMap<>();
        errorBody.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        errorBody.put("error", "Unauthorized");
        errorBody.put("message", authException.getMessage());
        errorBody.put("path", request.getServletPath());
        errorBody.put("timestamp", LocalDateTime.now().toString());

        objectMapper.writeValue(response.getOutputStream(), errorBody);
    }
}