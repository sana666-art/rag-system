package com.rag_system.service.impl;

import com.pgvector.PGvector;
import com.rag_system.builder.DocumentBuilder;
import com.rag_system.entity.PortfolioDocument;
import com.rag_system.repository.PortfolioDocumentRepository;
import com.rag_system.template.result.DocumentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GenericDocumentGenerationService {

    private final PortfolioDocumentRepository portfolioDocumentRepository;
    private final EmbeddingService embeddingService;

    public <E, D> int generateDocuments(List<E> entities,
                                        DocumentBuilder<E, D> builder) {

        int generated = 0;

        for (E entity : entities) {

            boolean exists =
                    portfolioDocumentRepository
                            .existsByDocumentSourceAndSourceId(
                                    builder.getDocumentSource(),
                                    builder.getSourceId(entity));

            if (exists) {
                continue;
            }

            DocumentResult result = builder.build(entity);

            float[] embeddingVector =
                    embeddingService.generateDocumentEmbedding(result.content());

            PGvector embedding =
                    new PGvector(embeddingVector);

            PortfolioDocument document =
                    PortfolioDocument.builder()
                            .userId(builder.getUserId(entity))
                            .portfolioId(builder.getPortfolioId(entity))
                            .documentSource(builder.getDocumentSource())
                            .sourceId(builder.getSourceId(entity))
                            .content(result.content())
                            .embedding(embedding)
                            .metadata(result.metadata() == null ? Map.of() : result.metadata())
                            .documentVersion(1)
                            .isDeleted(false)
                            .build();
            portfolioDocumentRepository.save(document);

            generated++;
        }

        return generated;
    }

    public <E, D> int regenerateDocuments(
            List<E> entities,
            DocumentBuilder<E, D> builder) {

        int regenerated = 0;

        for (E entity : entities) {

            Long sourceId = builder.getSourceId(entity);

            PortfolioDocument document =
                    portfolioDocumentRepository
                            .findByDocumentSourceAndSourceId(
                                    builder.getDocumentSource(),
                                    sourceId)
                            .orElse(null);

            if (document == null) {
                continue;
            }

            DocumentResult result = builder.build(entity);

            float[] embeddingVector =
                    embeddingService.generateDocumentEmbedding(
                            result.content());

            PGvector embedding =
                    new PGvector(embeddingVector);

            document.setUserId(builder.getUserId(entity));
            document.setPortfolioId(builder.getPortfolioId(entity));
            document.setContent(result.content());
            document.setEmbedding(embedding);
            document.setMetadata(
                    result.metadata() == null
                            ? Map.of()
                            : result.metadata());

            Integer currentVersion = document.getDocumentVersion();

            document.setDocumentVersion(
                    currentVersion == null
                            ? 1
                            : currentVersion + 1);

            document.setIsDeleted(false);

            portfolioDocumentRepository.save(document);

            regenerated++;
        }

        return regenerated;
    }

}
