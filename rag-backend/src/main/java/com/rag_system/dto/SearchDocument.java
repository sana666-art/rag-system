package com.rag_system.dto;

import com.rag_system.enums.DocumentSource;

import java.util.Map;

public record SearchDocument(
        Long id,
        DocumentSource source,
        Long sourceId,
        Long portfolioId,
        Long userId,
        String content,
        Map<String, Object> metadata,
        double similarity,
        int rank
) {}
