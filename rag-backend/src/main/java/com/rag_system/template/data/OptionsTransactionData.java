package com.rag_system.template.data;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OptionsTransactionData(
        Integer portfolioId,
        String portfolioName,
        Integer userId,
        String action,
        String optionTicker,
        Integer quantity,
        BigDecimal pricePerContract,
        BigDecimal totalCost,
        BigDecimal fee,
        BigDecimal realizedPnl,
        LocalDate executedAt,
        String note
) {}
