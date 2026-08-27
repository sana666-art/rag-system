package com.rag_system.dto.authDTO.response;

import lombok.*;

@Data
@Builder
public class LoginResponse {

    private boolean success;

    private boolean requiresTwoFactor;

    private String accessToken;

    private String refreshToken;

    private String message;
}
