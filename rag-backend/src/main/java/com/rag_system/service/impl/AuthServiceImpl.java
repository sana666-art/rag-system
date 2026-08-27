package com.rag_system.service.impl;

import com.rag_system.dto.authDTO.request.*;
import com.rag_system.dto.genericApiResonse.ApiResponse;
import com.rag_system.dto.authDTO.response.LoginResponse;
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
import com.rag_system.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmailService emailService;

    public ApiResponse register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new AppException("Passwords do not match", HttpStatus.BAD_REQUEST);
        }

        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new AppException("Email already exists", HttpStatus.CONFLICT);
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(UserRole.USER);
        user.setIsEmailVerified(false);
        user.setTwoFactorEnabled(false);
        user.setTokenVersion(0);
        user.setIsBlocked(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        String verificationOtp = generateOtp();
        Otp otp = new Otp();
        otp.setEmail(user.getEmail());
        otp.setOtpHash(passwordEncoder.encode(verificationOtp));
        otp.setExpiresAt(LocalDateTime.now().plusHours(24));
        otp.setVerified(false);
        otp.setPurpose(OtpPurpose.EMAIL_VERIFICATION);
        otp.setCreatedAt(LocalDateTime.now());
        otp.setUpdatedAt(LocalDateTime.now());
        otpRepository.save(otp);

        emailService.sendVerificationEmail(user.getEmail(), verificationOtp);

        return ApiResponse.success("User registered successfully. Please verify your email.");
    }

    public ApiResponse verifyEmail(String otpValue, VerifyEmailRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new AppException("User not found", HttpStatus.NOT_FOUND));

        if (Boolean.TRUE.equals(user.getIsEmailVerified())) {
            throw new AppException("Email is already verified", HttpStatus.BAD_REQUEST);
        }

        Otp otp = otpRepository
                .findByEmailAndPurposeAndVerifiedFalse(
                        request.getEmail(),
                        OtpPurpose.EMAIL_VERIFICATION
                )
                .orElseThrow(() ->
                        new AppException("Invalid verification code", HttpStatus.BAD_REQUEST));

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AppException("Verification code has expired", HttpStatus.BAD_REQUEST);
        }

        if (!passwordEncoder.matches(otpValue, otp.getOtpHash())) {
            throw new AppException("Invalid verification code", HttpStatus.BAD_REQUEST);
        }

        user.setIsEmailVerified(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        otp.setVerified(true);
        otp.setUpdatedAt(LocalDateTime.now());
        otpRepository.save(otp);

        return ApiResponse.success("Email verified successfully");
    }

    public ApiResponse resendVerification(ResendVerificationRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        if (Boolean.TRUE.equals(user.getIsEmailVerified())) {
            throw new AppException("Email is already verified", HttpStatus.BAD_REQUEST);
        }

        List<Otp> oldOtps = otpRepository.findByEmailAndPurpose(request.getEmail(), OtpPurpose.EMAIL_VERIFICATION);
        for (Otp oldOtp : oldOtps) {
            oldOtp.setVerified(true);
            oldOtp.setUpdatedAt(LocalDateTime.now());
            otpRepository.save(oldOtp);
        }

        String newOtp = generateOtp();
        Otp otp = new Otp();
        otp.setEmail(user.getEmail());
        otp.setOtpHash(passwordEncoder.encode(newOtp));
        otp.setExpiresAt(LocalDateTime.now().plusHours(24));
        otp.setVerified(false);
        otp.setPurpose(OtpPurpose.EMAIL_VERIFICATION);
        otp.setCreatedAt(LocalDateTime.now());
        otp.setUpdatedAt(LocalDateTime.now());
        otpRepository.save(otp);

        emailService.sendVerificationEmail(user.getEmail(), newOtp);

        return ApiResponse.success("Verification email sent");
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException("Invalid email or password", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }

        if (!Boolean.TRUE.equals(user.getIsEmailVerified())) {
            throw new AppException("Email not verified", HttpStatus.FORBIDDEN);
        }

        if (Boolean.TRUE.equals(user.getIsBlocked())) {
            throw new AppException("Account is blocked", HttpStatus.FORBIDDEN);
        }

        if (Boolean.TRUE.equals(user.getTwoFactorEnabled())) {
            String otpValue = generateOtp();
            Otp otp = new Otp();
            otp.setEmail(user.getEmail());
            otp.setOtpHash(passwordEncoder.encode(otpValue));
            otp.setExpiresAt(LocalDateTime.now().plusMinutes(10));
            otp.setVerified(false);
            otp.setPurpose(OtpPurpose.TWO_FACTOR_AUTH);
            otp.setCreatedAt(LocalDateTime.now());
            otp.setUpdatedAt(LocalDateTime.now());
            otpRepository.save(otp);

            emailService.send2FAOtpEmail(user.getEmail(), otpValue);

            return LoginResponse.builder()
                    .success(true)
                    .requiresTwoFactor(true)
                    .message("Verification code sent to your email")
                    .build();
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        Token refreshDbToken = new Token();
        refreshDbToken.setToken(refreshToken);
        refreshDbToken.setUserId(user);
        refreshDbToken.setType(TokenType.REFRESH);
        refreshDbToken.setExpires(LocalDateTime.now().plusDays(30));
        refreshDbToken.setBlacklisted(false);
        refreshDbToken.setCreatedAt(LocalDateTime.now());
        refreshDbToken.setUpdatedAt(LocalDateTime.now());
        tokenRepository.save(refreshDbToken);

        return LoginResponse.builder()
                .success(true)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public LoginResponse verify2FA(Verify2FARequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException("Invalid email or code", HttpStatus.BAD_REQUEST));

        Otp otp = otpRepository.findByEmailAndPurposeAndVerifiedFalse(request.getEmail(), OtpPurpose.TWO_FACTOR_AUTH)
                .orElseThrow(() -> new AppException("Invalid or expired code", HttpStatus.BAD_REQUEST));

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AppException("Code has expired", HttpStatus.BAD_REQUEST);
        }

        if (!passwordEncoder.matches(request.getCode(), otp.getOtpHash())) {
            throw new AppException("Invalid or expired code", HttpStatus.BAD_REQUEST);
        }

        otp.setVerified(true);
        otp.setUpdatedAt(LocalDateTime.now());
        otpRepository.save(otp);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        Token refreshDbToken = new Token();
        refreshDbToken.setToken(refreshToken);
        refreshDbToken.setUserId(user);
        refreshDbToken.setType(TokenType.REFRESH);
        refreshDbToken.setExpires(LocalDateTime.now().plusDays(30));
        refreshDbToken.setBlacklisted(false);
        refreshDbToken.setCreatedAt(LocalDateTime.now());
        refreshDbToken.setUpdatedAt(LocalDateTime.now());
        tokenRepository.save(refreshDbToken);

        return LoginResponse.builder()
                .success(true)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public LoginResponse refresh(RefreshTokenRequest request) {
        String refreshTokenValue = request.getRefreshToken();

        if (!jwtService.isTokenValid(refreshTokenValue)) {
            throw new AppException("Invalid or expired refresh token", HttpStatus.BAD_REQUEST);
        }

        Token dbToken = tokenRepository.findByTokenAndType(refreshTokenValue, TokenType.REFRESH)
                .orElseThrow(() -> new AppException("Refresh token not found", HttpStatus.BAD_REQUEST));

        if (Boolean.TRUE.equals(dbToken.getBlacklisted())) {
            throw new AppException("Refresh token has been revoked", HttpStatus.UNAUTHORIZED);
        }

        User user = dbToken.getUserId();
        String newAccessToken = jwtService.generateAccessToken(user);

        return LoginResponse.builder()
                .success(true)
                .accessToken(newAccessToken)
                .build();
    }

    public ApiResponse logout(RefreshTokenRequest request) {

        String refreshTokenValue = request.getRefreshToken();

        Token dbToken = tokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new AppException("Refresh token not found", HttpStatus.BAD_REQUEST));

        dbToken.setBlacklisted(true);
        dbToken.setUpdatedAt(LocalDateTime.now());
        tokenRepository.save(dbToken);

        return ApiResponse.success("Logged out successfully");
    }

    private String generateOtp() {
        int otp = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(otp);
    }
}
