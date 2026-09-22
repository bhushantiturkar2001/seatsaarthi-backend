package com.seatsaarthi.controller;

import com.seatsaarthi.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication and Token Generator Controller.
 * Provides JWT issuance for testing and passenger authentication.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for JWT issuance and token validation")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public record LoginRequest(String username, String role) {}
    public record AuthResponse(String accessToken, String tokenType, String username, long expiresInMs) {}

    @PostMapping("/login")
    @Operation(summary = "Generate JWT Token", description = "Generates a signed JWT Bearer token for API testing")
    public ResponseEntity<AuthResponse> login(@RequestBody(required = false) LoginRequest request) {
        String username = (request != null && request.username() != null) ? request.username() : "passenger@seatsaarthi.com";
        String role = (request != null && request.role() != null) ? request.role() : "ROLE_USER";

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        String token = jwtTokenProvider.generateToken(username, claims);
        return ResponseEntity.ok(new AuthResponse(token, "Bearer", username, 86400000L));
    }

    @GetMapping("/me")
    @Operation(summary = "Current Authenticated User", description = "Returns the currently authenticated user from SecurityContext")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal Object principal) {
        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", true);
        response.put("principal", principal);
        response.put("message", "Authentication successful via JWT Bearer token");
        return ResponseEntity.ok(response);
    }
}
