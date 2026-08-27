package com.rag_system.dto.ragDTO;

import java.util.List;

public record PortfolioHoldingsResult(
        List<HoldingResult> holdings
) {
    public static PortfolioHoldingsResult empty() {
        return new PortfolioHoldingsResult(List.of());
    }
}
