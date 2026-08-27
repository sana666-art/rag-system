package com.rag_system.dto.ragDTO;

import java.math.BigDecimal;

public record HoldingResult(
        String symbol,
        BigDecimal quantity
) {
}
