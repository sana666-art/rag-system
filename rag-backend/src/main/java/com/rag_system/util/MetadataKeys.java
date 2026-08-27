package com.rag_system.util;

import java.util.Set;

public final class MetadataKeys {

    // Common
    public static final String PORTFOLIO_ID = "portfolioId";
    public static final String PORTFOLIO_NAME = "portfolioName";
    public static final String USER_ID = "userId";
    public static final String TYPE = "type";

    // Stock
    public static final String SYMBOL = "symbol";

    // Options
    public static final String OPTION_TICKER = "optionTicker";
    public static final String UNDERLYING_TICKER = "underlyingTicker";
    public static final String CONTRACT_TYPE = "contractType";
    public static final String STATUS = "status";

    // Transactions
    public static final String ACTION = "action";

    private static final Set<String> ALLOWED = Set.of(
            PORTFOLIO_ID, PORTFOLIO_NAME, USER_ID, TYPE,
            SYMBOL, ACTION, OPTION_TICKER, UNDERLYING_TICKER,
            CONTRACT_TYPE, STATUS
    );

    public static boolean isAllowed(String key) {
        return ALLOWED.contains(key);
    }

    private MetadataKeys() {}
}
