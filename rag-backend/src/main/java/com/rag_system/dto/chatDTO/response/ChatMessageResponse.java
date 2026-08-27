package com.rag_system.dto.chatDTO.response;

import com.rag_system.enums.ChatMessageRole;

import java.time.LocalDateTime;
import java.util.Map;

public record ChatMessageResponse(
        Integer id,
        ChatMessageRole role,
        String content,
        LocalDateTime timestamp,
        Map<String, Object> uiWidget
) {}
