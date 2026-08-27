package com.rag_system.template.result;

import java.util.List;

public record RetrievalResult(
        List<SearchResult> results,
        long embeddingTime,
        long retrievalTime
) {}
