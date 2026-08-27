package com.rag_system.dto.chatDTO.response;

import com.rag_system.dto.ragDTO.RagResponse;

public record GuestAskResponse(
        RagResponse rag,
        int remainingQuota,
        int quotaLimit
) {}
