package com.rag_system.dto.chatDTO.request;

import jakarta.validation.constraints.NotBlank;

public record RenameSessionRequest(
        @NotBlank(message = "Title must not be blank")
        String title
) {}
