package com.rag_system.dto.chatDTO.response;

import java.time.LocalDateTime;

public record ChatSessionResponse(
        Integer id,
        String title,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String lastMessage,
        Long totalTokens
) {}
