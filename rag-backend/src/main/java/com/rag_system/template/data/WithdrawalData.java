package com.rag_system.template.data;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WithdrawalData(
        Integer portfolioId,
        String portfolioName,
        Integer userId,
        BigDecimal amount,
        LocalDate withdrawnAt,
        String note
) {}
