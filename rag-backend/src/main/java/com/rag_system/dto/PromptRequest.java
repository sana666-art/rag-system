package com.rag_system.dto;

import com.rag_system.template.result.SearchResult;

import java.util.List;

public record PromptRequest(
        String question,
        List<SearchResult> searchResults
) {}