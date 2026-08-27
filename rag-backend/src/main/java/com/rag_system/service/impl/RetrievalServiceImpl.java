package com.rag_system.service.impl;

import com.rag_system.dto.MetadataFilter;
import com.rag_system.dto.retrivalDTO.RetrievalRequest;
import com.rag_system.template.result.RetrievalResult;
import com.rag_system.repository.PortfolioSearchRepository;
import com.rag_system.service.RetrievalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetrievalServiceImpl implements RetrievalService {

    private final EmbeddingService embeddingService;
    private final PortfolioSearchRepository searchRepository;

    @Value("${rag.search.threshold}")
    private double threshold;

    @Value("${rag.search.top-k}")
    private int topK;

    @Override
    public RetrievalResult retrieve(
            Long userId, RetrievalRequest request) {

        log.debug(
                "Retrieval request: userId={} portfolioId={} source={} "
                        + "question='{}' filters={}",
                userId,
                request.portfolioId(),
                request.source(),
                request.question(),
                formatFilters(request.metadataFilters()));

        long embeddingStart = System.nanoTime();
        float[] questionEmbedding =
                embeddingService.generateQueryEmbedding(request.question());
        long embeddingTime = System.nanoTime() - embeddingStart;

        long retrievalStart = System.nanoTime();
        var results = searchRepository.findSimilarDocuments(
                userId,
                request.portfolioId(),
                request.source(),
                questionEmbedding,
                threshold,
                topK,
                request.metadataFilters());
        long retrievalTime = System.nanoTime() - retrievalStart;

        log.debug(
                "Retrieval result: userId={} question='{}' "
                        + "results={} embeddingTime={}ms retrievalTime={}ms",
                userId,
                request.question(),
                results.size(),
                embeddingTime / 1_000_000,
                retrievalTime / 1_000_000);

        return new RetrievalResult(
                results, embeddingTime, retrievalTime);
    }

    private String formatFilters(List<MetadataFilter> filters) {

        if (filters == null || filters.isEmpty()) {
            return "none";
        }

        StringBuilder sb = new StringBuilder("[");

        for (int i = 0; i < filters.size(); i++) {
            MetadataFilter f = filters.get(i);
            if (i > 0) sb.append(", ");
            sb.append(f.key())
              .append(" ")
              .append(f.operator())
              .append(" ")
              .append(f.value());
        }

        sb.append("]");
        return sb.toString();
    }
}
