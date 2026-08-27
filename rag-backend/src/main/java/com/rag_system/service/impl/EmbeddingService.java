package com.rag_system.service.impl;

import com.rag_system.dto.embeddingDTO.EmbeddingRequest;
import com.rag_system.dto.embeddingDTO.EmbeddingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmbeddingService {

    private final EmbeddingModel embeddingModel;

    private static final String DOCUMENT_TASK_PREFIX = "search_document: ";
    private static final String QUERY_TASK_PREFIX = "search_query: ";

    public float[] generateEmbedding(String text) {

        org.springframework.ai.embedding.EmbeddingResponse response =
                embeddingModel.embedForResponse(List.of(text));

        return response.getResults()
                .getFirst()
                .getOutput();
    }

    public float[] generateDocumentEmbedding(String text) {
        return generateEmbedding(DOCUMENT_TASK_PREFIX + text);
    }

    public float[] generateQueryEmbedding(String text) {
        return generateEmbedding(QUERY_TASK_PREFIX + text);
    }

    /**
     * Used for testing.
     */
    public EmbeddingResponse generateEmbeddingResponse(EmbeddingRequest text) {

        float[] embedding = generateEmbedding(text.getText());

        return EmbeddingResponse.builder()
                .dimensions(embedding.length)
                .embedding(embedding)
                .build();
    }

}