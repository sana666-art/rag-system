package com.rag_system.dto.authDTO.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class ResendVerificationRequest {

    @Email
    private String email;
}
