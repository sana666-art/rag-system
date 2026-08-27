package com.rag_system.dto.chatDTO.response;

import com.rag_system.dto.ragDTO.RagResponse;

public record ChatAskResponse(
        Integer sessionId,
        Integer userMessageId,
        Integer assistantMessageId,
        RagResponse rag
) {}
