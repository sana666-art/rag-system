package com.rag_system.dto.retrivalDTO;

import com.rag_system.dto.MetadataFilter;
import com.rag_system.enums.DocumentSource;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record RetrievalRequest(
        @NotBlank(message = "Question must not be blank")
        String question,
        Long portfolioId,
        DocumentSource source,
        List<MetadataFilter> metadataFilters
) {
    public static RetrievalRequest of(String question, Long userId) {
        return new RetrievalRequest(question, null, null, List.of());
    }
}
