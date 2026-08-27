package com.rag_system.dto.chatDTO.request;

import jakarta.validation.constraints.NotBlank;

public record GuestAskRequest(
        @NotBlank(message = "guestId must not be blank")
        String guestId,
        @NotBlank(message = "Question must not be blank")
        String question
) {}
