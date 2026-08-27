package com.rag_system.dto.chatDTO.request;

import com.rag_system.dto.MetadataFilter;
import com.rag_system.enums.DocumentSource;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record ChatRequest(
        @NotBlank(message = "Question must not be blank")
        String question,
        Long sessionId,
        Long portfolioId,
        DocumentSource source,
        List<MetadataFilter> metadataFilters
) {}
