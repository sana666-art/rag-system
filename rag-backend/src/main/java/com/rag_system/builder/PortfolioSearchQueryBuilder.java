package com.rag_system.builder;

import com.rag_system.dto.MetadataFilter;
import com.rag_system.enums.DocumentSource;
import com.rag_system.enums.FilterOperator;
import com.rag_system.util.MetadataKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class PortfolioSearchQueryBuilder {

    public record BuiltQuery(String sql, Map<String, Object> parameters) {}

    public BuiltQuery build(
            Long userId,
            Long portfolioId,
            DocumentSource source,
            float[] embedding,
            double threshold,
            int topK,
            List<MetadataFilter> metadataFilters) {

        if (metadataFilters != null) {
            for (MetadataFilter filter : metadataFilters) {
                validateMetadataFilter(filter);
            }
        }

        Map<String, Object> params = new LinkedHashMap<>();
        StringBuilder cte = new StringBuilder();
        cte.append("""
                WITH ranked AS (
                    SELECT
                        pd."id",
                        pd."userId",
                        pd."portfolioId",
                        pd."documentSource",
                        pd."sourceId",
                        pd.content,
                        pd."metadata",
                        1 - (pd.embedding <=> CAST(:embedding AS vector)) AS similarity
                    FROM "portfolioDocuments" pd
                    WHERE pd."isDeleted" = false
                      AND pd."userId" = :userId
                """);

        if (portfolioId != null) {
            cte.append("  AND pd.\"portfolioId\" = :portfolioId\n");
            params.put("portfolioId", portfolioId);
        }

        if (source != null) {
            cte.append("  AND pd.\"documentSource\" = :source\n");
            params.put("source", source.name());
        }

        if (metadataFilters != null) {
            for (int i = 0; i < metadataFilters.size(); i++) {
                MetadataFilter filter = metadataFilters.get(i);
                String condition = buildMetadataCondition(filter, i, params);
                log.debug(
                        "Metadata filter [{}]: key={} operator={} value={} → {}",
                        i,
                        filter.key(),
                        filter.operator(),
                        filter.value(),
                        condition);
                cte.append("  AND ");
                cte.append(condition);
                cte.append("\n");
            }
        }

        cte.append("""
                )
                SELECT * FROM ranked
                WHERE similarity >= :threshold
                ORDER BY similarity DESC
                LIMIT :topK
                """);

        params.put("embedding", embedding);
        params.put("userId", userId);
        params.put("threshold", threshold);
        params.put("topK", topK);

        log.debug("QueryBuilder filters applied: {}", metadataFilters != null ? metadataFilters.size() : 0);

        return new BuiltQuery(cte.toString(), params);
    }

    private void validateMetadataFilter(MetadataFilter filter) {
        if (filter.key() == null || filter.key().isBlank()) {
            throw new IllegalArgumentException(
                    "Metadata filter key must not be blank.");
        }

        if (!MetadataKeys.isAllowed(filter.key())) {
            throw new IllegalArgumentException(
                    "Unknown metadata key: '" + filter.key() + "'");
        }

        switch (filter.operator()) {

            case BETWEEN -> {
                List<?> values = toList(filter.value());

                if (values.size() != 2) {
                    throw new IllegalArgumentException(
                            "BETWEEN requires exactly two values, got " +
                                    values.size() + " for key '" +
                                    filter.key() + "'");
                }

                for (Object v : values) {
                    if (!isNumeric(v)) {
                        throw new IllegalArgumentException(
                                "BETWEEN requires numeric values, got '" +
                                        v + "' for key '" +
                                        filter.key() + "'");
                    }
                }
            }

            case IN -> {
                List<?> values = toList(filter.value());

                if (values.isEmpty()) {
                    throw new IllegalArgumentException(
                            "IN requires at least one value.");
                }
            }

            case EXISTS -> {
                if (filter.value() != null) {
                    throw new IllegalArgumentException(
                            "EXISTS does not accept a value.");
                }
            }

            case GT, GTE, LT, LTE -> {
                if (!isNumeric(filter.value())) {
                    throw new IllegalArgumentException(
                            filter.operator() + " requires a numeric value, got " +
                                    filter.value() + " for key '" +
                                    filter.key() + "'");
                }
            }

            default -> {
            }
        }
    }

    private String buildMetadataCondition(
            MetadataFilter filter, int index, Map<String, Object> params) {

        String key = filter.key();
        FilterOperator op = filter.operator();
        String prefix = "meta_" + index;

        return switch (op) {
            case EQ -> {
                params.put(prefix, filter.value().toString());
                yield String.format(
                        "pd.\"metadata\"->>'%s' = :%s",
                        key, prefix);
            }
            case GT -> {
                params.put(prefix, filter.value().toString());
                yield String.format(
                        "(pd.\"metadata\"->>'%s')::numeric > :%s",
                        key, prefix);
            }
            case GTE -> {
                params.put(prefix, filter.value().toString());
                yield String.format(
                        "(pd.\"metadata\"->>'%s')::numeric >= :%s",
                        key, prefix);
            }
            case LT -> {
                params.put(prefix, filter.value().toString());
                yield String.format(
                        "(pd.\"metadata\"->>'%s')::numeric < :%s",
                        key, prefix);
            }
            case LTE -> {
                params.put(prefix, filter.value().toString());
                yield String.format(
                        "(pd.\"metadata\"->>'%s')::numeric <= :%s",
                        key, prefix);
            }
            case IN -> {
                List<?> values = toList(filter.value());
                StringBuilder placeholders = new StringBuilder();
                for (int i = 0; i < values.size(); i++) {
                    String paramName = prefix + "_v" + i;
                    params.put(paramName, values.get(i).toString());
                    if (i > 0) placeholders.append(", ");
                    placeholders.append(":").append(paramName);
                }
                yield String.format(
                        "pd.\"metadata\"->>'%s' IN (%s)",
                        key, placeholders);
            }
            case BETWEEN -> {
                List<?> range = toList(filter.value());
                String low = prefix + "_low";
                String high = prefix + "_high";
                params.put(low, range.get(0).toString());
                params.put(high, range.get(1).toString());
                yield String.format(
                        "(pd.\"metadata\"->>'%s')::numeric BETWEEN :%s AND :%s",
                        key, low, high);
            }
            case CONTAINS -> {
                params.put(prefix, "%" + filter.value() + "%");
                yield String.format(
                        "pd.\"metadata\"->>'%s' ILIKE :%s",
                        key, prefix);
            }
            case STARTS_WITH -> {
                params.put(prefix, filter.value() + "%");
                yield String.format(
                        "pd.\"metadata\"->>'%s' ILIKE :%s",
                        key, prefix);
            }
            case ENDS_WITH -> {
                params.put(prefix, "%" + filter.value());
                yield String.format(
                        "pd.\"metadata\"->>'%s' ILIKE :%s",
                        key, prefix);
            }
            case EXISTS -> {
                yield String.format(
                        "pd.\"metadata\" ? '%s'", key);
            }
        };
    }

    @SuppressWarnings("unchecked")
    private List<?> toList(Object value) {
        if (value instanceof List<?> list) return list;
        if (value instanceof Collection<?> col)
            return new ArrayList<>(col);
        if (value.getClass().isArray())
            return Arrays.asList((Object[]) value);
        return List.of(value);
    }

    private boolean isNumeric(Object value) {
        if (value instanceof Number) return true;
        if (value instanceof String s) {
            try {
                Double.parseDouble(s);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }
}
