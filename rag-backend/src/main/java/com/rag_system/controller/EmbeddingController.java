package com.rag_system.controller;

import com.rag_system.dto.embeddingDTO.EmbeddingRequest;
import com.rag_system.dto.embeddingDTO.EmbeddingResponse;
import com.rag_system.service.impl.EmbeddingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/embeddings")
@RequiredArgsConstructor
public class EmbeddingController {

    private final EmbeddingService embeddingService;

    @PostMapping("/test")
    public ResponseEntity<EmbeddingResponse> testEmbedding(
            @Valid @RequestBody EmbeddingRequest text) {

        return ResponseEntity.ok(
                embeddingService.generateEmbeddingResponse(text)
        );
    }

}