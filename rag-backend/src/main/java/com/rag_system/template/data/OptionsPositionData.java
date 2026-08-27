package com.rag_system.template.data;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OptionsPositionData(
        Integer portfolioId,
        String portfolioName,
        Integer userId,
        String optionTicker,
        String underlyingTicker,
        String contractType,
        BigDecimal strikePrice,
        LocalDate expirationDate,
        String side,
        Integer quantity,
        BigDecimal avgCostPerContract,
        String status,
        BigDecimal realizedPnl
) {}
