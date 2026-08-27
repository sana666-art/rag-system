package com.rag_system.dto.ragDTO;

import com.rag_system.dto.SearchDocument;

import java.util.List;

public record RagResponse(
        String answer,
        String model,
        List<SearchDocument> sources,
        int contextDocuments,
        double averageSimilarity,
        double highestSimilarity,
        int promptCharacters,
        int estimatedPromptTokens,
        int promptTokens,
        int completionTokens,
        int totalTokens,
        long embeddingTime,
        long retrievalTime,
        long llmTime,
        long totalTime
) {}
