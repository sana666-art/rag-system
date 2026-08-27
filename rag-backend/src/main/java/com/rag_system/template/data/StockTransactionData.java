package com.rag_system.template.data;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StockTransactionData(
        Integer portfolioId,
        String portfolioName,
        Integer userId,
        String type,
        String symbol,
        BigDecimal quantity,
        BigDecimal price,
        BigDecimal fee,
        String source,
        LocalDate executedAt,
        String note
) {}
