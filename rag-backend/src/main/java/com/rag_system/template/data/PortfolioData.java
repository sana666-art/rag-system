package com.rag_system.template.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PortfolioData(
        String name,
        Integer userId,
        BigDecimal initialCapital,
        BigDecimal cashBalance,
        String color,
        LocalDate createdAt
) {}
