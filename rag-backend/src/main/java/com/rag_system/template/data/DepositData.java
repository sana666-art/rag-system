package com.rag_system.template.data;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DepositData(
        Integer portfolioId,
        String portfolioName,
        Integer userId,
        BigDecimal amount,
        LocalDate depositedAt,
        String note
) {}
