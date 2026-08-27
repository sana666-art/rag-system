package com.rag_system.service;

import com.rag_system.dto.ragDTO.PortfolioQueryAnalysis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Component
public class PortfolioQueryAnalyzer {

    private static final Map<String, String> COMPANY_TO_SYMBOL = Map.ofEntries(
            Map.entry("tesla", "TSLA"),
            Map.entry("apple", "AAPL"),
            Map.entry("microsoft", "MSFT"),
            Map.entry("google", "GOOGL"),
            Map.entry("nvidia", "NVDA"),
            Map.entry("amazon", "AMZN"),
            Map.entry("meta", "META"),
            Map.entry("netflix", "NFLX"),
            Map.entry("amd", "AMD"),
            Map.entry("intel", "INTC"),
            Map.entry("salesforce", "CRM"),
            Map.entry("adobe", "ADBE"),
            Map.entry("paypal", "PYPL"),
            Map.entry("nike", "NKE"),
            Map.entry("coca cola", "KO"),
            Map.entry("disney", "DIS"),
            Map.entry("walmart", "WMT"),
            Map.entry("jpmorgan", "JPM"),
            Map.entry("goldman sachs", "GS"),
            Map.entry("berkshire", "BRK.B"),
            Map.entry("johnson", "JNJ"),
            Map.entry("visa", "V"),
            Map.entry("mastercard", "MA"),
            Map.entry("unitedhealth", "UNH"),
            Map.entry("procter", "PG"),
            Map.entry("walt disney", "DIS"),
            Map.entry("boeing", "BA"),
            Map.entry("caterpillar", "CAT"),
            Map.entry("ibm", "IBM"),
            Map.entry("uber", "UBER"),
            Map.entry("lyft", "LYFT"),
            Map.entry("airbnb", "ABNB"),
            Map.entry("snap", "SNAP"),
            Map.entry("spotify", "SPOT"),
            Map.entry("shopify", "SHOP"),
            Map.entry("block", "SQ"),
            Map.entry("robinhood", "HOOD"),
            Map.entry("coinbase", "COIN"),
            Map.entry("moderna", "MRNA"),
            Map.entry("pfizer", "PFE"),
            Map.entry("abbvie", "ABBV"),
            Map.entry("merck", "MRK")
    );

    public PortfolioQueryAnalysis analyze(String question) {

        if (question == null) {
            return new PortfolioQueryAnalysis(null, "GENERAL_STOCK", null);
        }

        String lowerQuestion = question.toLowerCase(Locale.ROOT);

        String symbol = extractSymbol(lowerQuestion);
        String queryType = determineQueryType(lowerQuestion);
        String action = determineAction(lowerQuestion, queryType);
        String contractType = extractContractType(lowerQuestion);
        String status = extractStatus(lowerQuestion);

        log.debug(
                "Portfolio query analyzed: question='{}' queryType={} symbol={} "
                        + "action={} contractType={} status={}",
                question, queryType, symbol,
                action, contractType, status);

        return new PortfolioQueryAnalysis(
                symbol, queryType, action,
                null, symbol, contractType, status);
    }

    private String extractSymbol(String question) {

        String upperQuestion = question.toUpperCase(Locale.ROOT);

        for (String symbol : COMPANY_TO_SYMBOL.values()) {

            if (Pattern.compile(
                            "\\b" + Pattern.quote(symbol) + "\\b")
                    .matcher(upperQuestion)
                    .find()) {

                return symbol;
            }
        }

        String lowerQuestion = question.toLowerCase(Locale.ROOT);

        for (Map.Entry<String, String> entry :
                COMPANY_TO_SYMBOL.entrySet()) {

            if (lowerQuestion.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        return null;
    }

    private String determineQueryType(String question) {

        if (containsAny(
                question,
                "deposit",
                "deposited",
                "deposits",
                "funded",
                "funding",
                "added money",
                "add funds")) {

            return "DEPOSITS";
        }

        if (containsAny(
                question,
                "withdrawal",
                "withdrew",
                "withdrawals",
                "withdraw",
                "removed money",
                "take out",
                "took out")) {

            return "WITHDRAWALS";
        }

        if (containsAny(
                question,
                "option position",
                "options position",
                "option positions",
                "options positions",
                "options holdings",
                "option holdings",
                "open options",
                "closed options",
                "option contract",
                "options contract")) {

            return "OPTIONS_POSITIONS";
        }

        if (isOptionsTransactionQuestion(question)) {
            return "OPTIONS_TRANSACTIONS";
        }

        if (containsAny(
                question,
                "how many shares",
                "how much stock",
                "how many stock",
                "how many shares do i have",
                "how much do i own",
                "what do i own",
                "what stocks do i own",
                "my holdings",
                "holdings",
                "current position",
                "current positions")) {

            return "HOLDINGS";
        }

        if (containsAny(
                question,
                "transaction",
                "transactions",
                "what did we do",
                "what happened",
                "bought",
                "buy",
                "sold",
                "sell",
                "purchase",
                "purchased",
                "trade",
                "traded",
                "trading",
                "last bought",
                "last purchase")) {

            return "TRANSACTIONS";
        }

        return "GENERAL_STOCK";
    }

    private boolean isOptionsTransactionQuestion(String question) {

        boolean mentionsOptions =
                containsAny(question, "option", "options");

        boolean mentionsTransaction = containsAny(
                question,
                "transaction",
                "transactions",
                "trade",
                "trades",
                "buy",
                "bought",
                "buying",
                "sell",
                "sold",
                "selling",
                "purchase",
                "purchased",
                "exercised",
                "exercise");

        return mentionsOptions && mentionsTransaction;
    }

    private String determineAction(
            String question,
            String queryType) {

        if ("OPTIONS_TRANSACTIONS".equals(queryType)) {

            if (containsAny(
                    question,
                    "exercised",
                    "exercise",
                    "exercises")) {

                return "EXERCISED_MANUAL";
            }

            if (containsAny(
                    question,
                    "sell",
                    "sold",
                    "selling")) {

                return "SELL_TO_CLOSE";
            }

            if (containsAny(
                    question,
                    "buy",
                    "bought",
                    "buying",
                    "purchase",
                    "purchased")) {

                return "BUY_TO_OPEN";
            }

            return null;
        }

        if (containsAny(
                question,
                "sell",
                "sold",
                "selling")) {

            return "SELL";
        }

        if (containsAny(
                question,
                "buy",
                "bought",
                "buying",
                "purchase",
                "purchased")) {

            return "BUY";
        }

        return null;
    }

    private String extractContractType(String question) {

        if (containsAny(question, "call", "calls")) {
            return "CALL";
        }

        if (containsAny(question, "put", "puts")) {
            return "PUT";
        }

        return null;
    }

    private String extractStatus(String question) {

        if (containsAny(question, "open", "opened", "active")) {
            return "OPEN";
        }

        if (containsAny(question, "closed", "close", "expired")) {
            return "CLOSED";
        }

        if (containsAny(question, "exercised", "exercise", "exercises")) {
            return "EXERCISED";
        }

        return null;
    }

    private boolean containsAny(
            String question,
            String... phrases) {

        for (String phrase : phrases) {

            if (question.contains(phrase)) {
                return true;
            }
        }

        return false;
    }
}
