package com.rag_system.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rag_system.builder.PortfolioSearchQueryBuilder;
import com.rag_system.dto.MetadataFilter;
import com.rag_system.dto.SearchDocument;
import com.rag_system.enums.DocumentSource;
import com.rag_system.template.result.SearchResult;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.TupleTransformer;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
public class PortfolioSearchRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final PortfolioSearchQueryBuilder queryBuilder;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public PortfolioSearchRepository(
            PortfolioSearchQueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    public List<SearchResult> findSimilarDocuments(
            Long userId,
            Long portfolioId,
            DocumentSource source,
            float[] embedding,
            double threshold,
            int topK,
            List<MetadataFilter> metadataFilters) {

        PortfolioSearchQueryBuilder.BuiltQuery built =
                queryBuilder.build(
                        userId, portfolioId, source,
                        embedding, threshold, topK,
                        metadataFilters);

        log.debug("Repository built SQL:\n{}", built.sql());
        log.debug("Repository parameters: {}", built.parameters());

        Session session = entityManager.unwrap(Session.class);

        @SuppressWarnings({"unchecked", "rawtypes"})
        NativeQuery<Map<String, Object>> query =
                session.createNativeQuery(built.sql());

        for (Map.Entry<String, Object> entry :
                built.parameters().entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        query.setTupleTransformer(
                (TupleTransformer<Map<String, Object>>)
                        (tuple, aliases) -> {
                            Map<String, Object> map =
                                    new LinkedHashMap<>();
                            for (int i = 0; i < aliases.length; i++) {
                                map.put(aliases[i], tuple[i]);
                            }
                            return map;
                        });

        List<Map<String, Object>> rows = query.list();

        log.debug("Repository raw rows returned: {}", rows.size());

        for (Map<String, Object> row : rows) {
            log.debug(
                    "Row: id={} sourceId={} portfolioId={} "
                            + "documentSource={} similarity={} "
                            + "metadata={}",
                    row.get("id"),
                    row.get("sourceId"),
                    row.get("portfolioId"),
                    row.get("documentSource"),
                    row.get("similarity"),
                    row.get("metadata"));
        }

        List<SearchResult> results = new ArrayList<>(rows.size());
        int rank = 1;

        for (Map<String, Object> row : rows) {
            SearchDocument doc = mapToDocument(row, rank);
            results.add(new SearchResult(doc, doc.similarity(), doc.rank()));
            rank++;
        }

        log.debug(
                "Repository final results: count={} "
                        + "types={} statuses={}",
                results.size(),
                results.stream()
                        .map(r -> r.document().metadata().get("type"))
                        .distinct()
                        .toList(),
                results.stream()
                        .map(r -> r.document().metadata().get("status"))
                        .distinct()
                        .toList());

        return results;
    }

    @SuppressWarnings("unchecked")
    private SearchDocument mapToDocument(Map<String, Object> row, int rank) {
        return new SearchDocument(
                toLong(row.get("id")),
                DocumentSource.valueOf(
                        row.get("documentSource").toString()),
                toLong(row.get("sourceId")),
                toLong(row.get("portfolioId")),
                toLong(row.get("userId")),
                (String) row.get("content"),
                toMetadata(row.get("metadata")),
                ((Number) row.get("similarity")).doubleValue(),
                rank
        );
    }

    private Map<String, Object> toMetadata(Object value) {
        if (value == null) {
            return Map.of();
        }
        if (value instanceof Map<?, ?> map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) map;
            return result;
        }
        if (value instanceof String json) {
            try {
                return OBJECT_MAPPER.readValue(
                        json, new TypeReference<>() {});
            } catch (JsonProcessingException e) {
                return Map.of();
            }
        }
        return Map.of();
    }

    private Long toLong(Object value) {
        return value != null ? ((Number) value).longValue() : null;
    }
}
