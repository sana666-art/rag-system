package com.rag_system.dto;

import com.rag_system.enums.FilterOperator;

public record MetadataFilter(
        String key,
        FilterOperator operator,
        Object value
) {}
