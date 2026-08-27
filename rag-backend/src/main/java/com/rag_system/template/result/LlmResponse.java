package com.rag_system.template.result;

public record LlmResponse(
        String answer,
        String model,
        int promptTokens,
        int completionTokens,
        int totalTokens
) {}
