package com.rag_system.service;

import com.rag_system.dto.authDTO.request.*;
import com.rag_system.dto.authDTO.response.LoginResponse;
import com.rag_system.dto.genericApiResonse.ApiResponse;
import com.rag_system.entity.Otp;
import com.rag_system.entity.Token;
import com.rag_system.entity.User;
import com.rag_system.enums.OtpPurpose;
import com.rag_system.enums.TokenType;
import com.rag_system.enums.UserRole;
import com.rag_system.exception.AppException;
import com.rag_system.repository.OtpRepository;
import com.rag_system.repository.TokenRepository;
import com.rag_system.repository.UserRepository;
import com.rag_system.service.impl.EmailService;
import com.rag_system.service.impl.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public interface AuthService {

    ApiResponse register(RegisterRequest request);

    ApiResponse verifyEmail(String otpValue, VerifyEmailRequest request);

    ApiResponse resendVerification(ResendVerificationRequest request);

    LoginResponse login(LoginRequest request);

    LoginResponse verify2FA(Verify2FARequest request);

    LoginResponse refresh(RefreshTokenRequest request);

    ApiResponse logout(RefreshTokenRequest request);
}
