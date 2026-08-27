package com.rag_system.dto.embeddingDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmbeddingRequest {

    @NotBlank(message = "Text is required")
    private String text;

}