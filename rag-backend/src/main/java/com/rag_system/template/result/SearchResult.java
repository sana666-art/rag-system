package com.rag_system.template.result;

import com.rag_system.dto.SearchDocument;

public record SearchResult(
        SearchDocument document,
        double similarity,
        int rank
) {}
