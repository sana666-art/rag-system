package com.rag_system.controller;

import com.rag_system.dto.authDTO.request.*;
import com.rag_system.dto.genericApiResonse.ApiResponse;
import com.rag_system.dto.authDTO.response.LoginResponse;
import com.rag_system.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request) {

        return ResponseEntity.ok(
                authService.verifyEmail(request.getOtp(), request)
        );
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse> resendVerification(
            @Valid @RequestBody ResendVerificationRequest request) {

        return ResponseEntity.ok(authService.resendVerification(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/verify-2fa")
    public ResponseEntity<LoginResponse> verify2FA(
            @Valid @RequestBody Verify2FARequest request) {

        return ResponseEntity.ok(authService.verify2FA(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {

        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(
            @Valid @RequestBody RefreshTokenRequest request) {

        return ResponseEntity.ok(authService.logout(request));
    }
}
