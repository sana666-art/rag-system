package com.rag_system.service;

import com.rag_system.dto.ragDTO.PortfolioQueryAnalysis;
import com.rag_system.enums.QuestionIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class QuestionClassifier {

    private final PortfolioQueryAnalyzer portfolioQueryAnalyzer;

    private static final List<String> PORTFOLIO_KEYWORDS = List.of(
            "portfolio", "deposit", "withdraw", "transaction", "option",
            "position", "stock", "share", "fund", "investment", "invest",
            "balance", "holding", "trade", "trading", "buy", "sell",
            "equity", "dividend", "profit", "loss", "performance", "asset",
            "retirement", "ira", "etf", "bond", "index", "interest",
            "premium", "strike", "expiry", "call", "put", "spread",
            "margin", "allocation", "diversif", "risk", "return", "gain",
            "earn", "worth", "apy", "annual", "quarterly", "monthly",
            "quantity", "lot", "payout", "yield", "principal", "contribut"
    );

    private static final List<Pattern> APPLICATION_INFO_PATTERNS = List.of(

            // Identity
            Pattern.compile(
                    "\\b(who\\s+are\\s+you|what\\s+are\\s+you"
                            + "|are\\s+you\\s+(an?\\s+)?"
                            + "(ai|robot|human|chatbot|bot|assistant|real\\s+person))\\b",
                    Pattern.CASE_INSENSITIVE),

            // Name / creator
            Pattern.compile(
                    "\\b(what\\s+is\\s+your\\s+name|your\\s+name"
                            + "|who\\s+made\\s+you"
                            + "|who\\s+created\\s+you"
                            + "|what\\s+are\\s+you\\s+made\\s+of)\\b",
                    Pattern.CASE_INSENSITIVE),

            // Capabilities
            Pattern.compile(
                    "\\b(how\\s+can\\s+you\\s+help"
                            + "|what\\s+can\\s+you\\s+do"
                            + "|what\\s+do\\s+you\\s+do"
                            + "|how\\s+can\\s+you\\s+assist"
                            + "|what\\s+can\\s+you\\s+help\\s+(me\\s+)?with"
                            + "|what\\s+are\\s+you\\s+capable\\s+of"
                            + "|how\\s+do\\s+you\\s+work"
                            + "|how\\s+does\\s+this\\s+work"
                            + "|what\\s+is\\s+your\\s+purpose"
                            + "|can\\s+you\\s+help\\s+me"
                            + "|help\\s+me"
                            + "|tell\\s+me\\s+about\\s+(yourself|you)"
                            + "|introduce\\s+yourself)\\b",
                    Pattern.CASE_INSENSITIVE),

            // Application / platform
            Pattern.compile(
                    "\\b(what\\s+is\\s+(this|this\\s+app|this\\s+tool"
                            + "|this\\s+website|this\\s+platform)"
                            + "|what\\s+does\\s+this\\s+(app|tool|site|platform)\\s+do"
                            + "|tell\\s+me\\s+about\\s+this\\s+(app|tool|site|platform)"
                            + "|tell\\s+me\\s+about\\s+(the\\s+)?rag\\s+assistant"
                            + "|what\\s+is\\s+the\\s+rag\\s+assistant"
                            + "|what\\s+is\\s+this\\s+rag\\s+application)\\b",
                    Pattern.CASE_INSENSITIVE)
    );

    private static final List<Pattern> GENERAL_PATTERNS = List.of(

            // Greetings
            Pattern.compile(
                    "^(hi|hello|hey|yo|howdy|greetings|hiya|hola"
                            + "|good\\s+(morning|afternoon|evening|night)"
                            + "|whats?\\s+up|sup|heyy+|hell+o+)\\b.*",
                    Pattern.CASE_INSENSITIVE),

            // Casual conversation
            Pattern.compile(
                    "\\b(how\\s+are\\s+you"
                            + "|hows?\\s+it\\s+going"
                            + "|how\\s+are\\s+you\\s+doing"
                            + "|how\\s+have\\s+you\\s+been)\\b",
                    Pattern.CASE_INSENSITIVE),

            // Thanks
            Pattern.compile(
                    "\\b(thanks|thank\\s+you|thankyou|thx|ty|appreciate\\s+it)\\b",
                    Pattern.CASE_INSENSITIVE),

            // Goodbye
            Pattern.compile(
                    "\\b(bye|goodbye|good\\s+bye|see\\s+you|see\\s+ya"
                            + "|have\\s+a\\s+good\\s+(day|one))\\b",
                    Pattern.CASE_INSENSITIVE),

            // Casual / playful
            Pattern.compile(
                    "\\b(are\\s+you\\s+(crazy|mad|okay|alright|real|there)"
                            + "|do\\s+you\\s+understand"
                            + "|do\\s+you\\s+know\\s+me)\\b",
                    Pattern.CASE_INSENSITIVE)
    );

    public QuestionIntent classify(String question) {

        if (question == null) {
            return QuestionIntent.UNKNOWN;
        }

        String normalized = question
                .toLowerCase(Locale.ROOT)
                .trim();

        if (normalized.isEmpty()) {
            return QuestionIntent.UNKNOWN;
        }

        String expanded = expandAbbreviations(normalized);

        /*
         * IMPORTANT:
         *
         * Application/general patterns are checked BEFORE portfolio
         * keywords.
         *
         * Example:
         *
         * "What is this investment application?"
         *
         * contains "investment", but it is still an application
         * question and must not go through portfolio RAG.
         */

        if (matches(APPLICATION_INFO_PATTERNS, expanded)) {
            return QuestionIntent.APPLICATION_INFO;
        }

        if (matches(GENERAL_PATTERNS, expanded)) {
            return QuestionIntent.GENERAL;
        }

        if (containsPortfolioKeyword(expanded)) {
            return QuestionIntent.PORTFOLIO;
        }

        PortfolioQueryAnalysis portfolioAnalysis =
                portfolioQueryAnalyzer.analyze(expanded);

        if (portfolioAnalysis.symbol() != null) {
            return QuestionIntent.PORTFOLIO;
        }

        return QuestionIntent.UNKNOWN;
    }

    private boolean containsPortfolioKeyword(String question) {

        return PORTFOLIO_KEYWORDS.stream()
                .anyMatch(question::contains);
    }

    private boolean matches(
            List<Pattern> patterns,
            String question) {

        return patterns.stream()
                .anyMatch(pattern ->
                        pattern.matcher(question).find());
    }

    private String expandAbbreviations(String normalized) {

        return normalized
                .replaceAll("\\bu\\b", "you")
                .replaceAll("\\br\\b", "are");
    }
}
