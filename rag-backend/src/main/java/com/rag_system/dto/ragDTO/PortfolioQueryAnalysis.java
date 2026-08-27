package com.rag_system.dto.ragDTO;

public record PortfolioQueryAnalysis(
        String symbol,
        String queryType,
        String action,
        String optionTicker,
        String underlyingTicker,
        String contractType,
        String status
) {

    public PortfolioQueryAnalysis(String symbol, String queryType, String action) {
        this(symbol, queryType, action, null, null, null, null);
    }
}
