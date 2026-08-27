package com.rag_system.service.impl;

import com.rag_system.dto.MetadataFilter;
import com.rag_system.dto.PromptRequest;
import com.rag_system.dto.SearchDocument;
import com.rag_system.dto.ragDTO.HoldingResult;
import com.rag_system.dto.ragDTO.PortfolioHoldingsResult;
import com.rag_system.dto.ragDTO.PortfolioQueryAnalysis;
import com.rag_system.dto.ragDTO.RagResponse;
import com.rag_system.dto.retrivalDTO.RetrievalRequest;
import com.rag_system.enums.FilterOperator;
import com.rag_system.enums.QuestionIntent;
import com.rag_system.service.LlmService;
import com.rag_system.service.PortfolioCalculationService;
import com.rag_system.service.PortfolioQueryAnalyzer;
import com.rag_system.service.QuestionClassifier;
import com.rag_system.service.RagService;
import com.rag_system.service.RetrievalService;
import com.rag_system.template.prompt.GeneralPromptTemplate;
import com.rag_system.template.prompt.PortfolioPromptTemplate;
import com.rag_system.template.result.LlmResponse;
import com.rag_system.template.result.PromptResult;
import com.rag_system.template.result.RetrievalResult;
import com.rag_system.template.result.SearchResult;
import com.rag_system.util.MetadataKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RagServiceImpl implements RagService {

    private static final Logger log =
            LoggerFactory.getLogger(RagServiceImpl.class);

    private final RetrievalService retrievalService;
    private final PortfolioPromptTemplate promptTemplate;
    private final GeneralPromptTemplate generalPromptTemplate;
    private final LlmService llmService;
    private final QuestionClassifier questionClassifier;
    private final PortfolioQueryAnalyzer portfolioQueryAnalyzer;
    private final PortfolioCalculationService portfolioCalculationService;

    public RagServiceImpl(
            RetrievalService retrievalService,
            PortfolioPromptTemplate promptTemplate,
            GeneralPromptTemplate generalPromptTemplate,
            LlmService llmService,
            QuestionClassifier questionClassifier,
            PortfolioQueryAnalyzer portfolioQueryAnalyzer,
            PortfolioCalculationService portfolioCalculationService) {
        this.retrievalService = retrievalService;
        this.promptTemplate = promptTemplate;
        this.generalPromptTemplate = generalPromptTemplate;
        this.llmService = llmService;
        this.questionClassifier = questionClassifier;
        this.portfolioQueryAnalyzer = portfolioQueryAnalyzer;
        this.portfolioCalculationService = portfolioCalculationService;
    }

    @Override
    public RagResponse ask(Long userId, RetrievalRequest request) {

        QuestionIntent intent =
                questionClassifier.classify(request.question());

        log.debug(
                "Question classified: question='{}', intent={}",
                request.question(),
                intent);

        return switch (intent) {

            case GENERAL ->
                    answerGeneral(request);

            case APPLICATION_INFO ->
                    answerApplicationInfo(request);

            case PORTFOLIO ->
                    answerPortfolio(userId, request);

            case UNKNOWN ->
                    answerUnknown(request);
        };
    }

    private RagResponse answerGeneral(RetrievalRequest request) {

        long totalStart = System.nanoTime();

        PromptResult promptResult = generalPromptTemplate.build(
                new PromptRequest(request.question(), List.of()));

        long llmStart = System.nanoTime();
        LlmResponse llm =
                llmService.generate(promptResult.prompt());
        long llmTime = System.nanoTime() - llmStart;

        long totalTime = System.nanoTime() - totalStart;

        log.info(
                "RAG general model={} promptChars={} promptTokens(est)={} "
                        + "promptTokens(actual)={} completionTokens={} totalTokens={} "
                        + "answerChars={} llmTime={}ms totalTime={}ms",
                llm.model(),
                promptResult.promptCharacters(),
                promptResult.estimatedPromptTokens(),
                llm.promptTokens(),
                llm.completionTokens(),
                llm.totalTokens(),
                llm.answer().length(),
                llmTime / 1_000_000,
                totalTime / 1_000_000);

        return new RagResponse(
                llm.answer(),
                llm.model(),
                List.of(),
                0,
                0.0,
                0.0,
                promptResult.promptCharacters(),
                promptResult.estimatedPromptTokens(),
                llm.promptTokens(),
                llm.completionTokens(),
                llm.totalTokens(),
                0,
                0,
                llmTime / 1_000_000,
                totalTime / 1_000_000);
    }

    private RagResponse answerPortfolio(Long userId, RetrievalRequest request) {

        PortfolioQueryAnalysis analysis =
                portfolioQueryAnalyzer.analyze(request.question());

        return switch (analysis.queryType()) {

            case "HOLDINGS" ->
                    answerHoldings(userId, request, analysis);

            case "DEPOSITS" ->
                    answerDeposits(userId, request, analysis);

            case "WITHDRAWALS" ->
                    answerWithdrawals(userId, request, analysis);

            case "OPTIONS_POSITIONS" ->
                    answerOptionsPositions(userId, request, analysis);

            case "OPTIONS_TRANSACTIONS" ->
                    answerOptionsTransactions(userId, request, analysis);

            default ->
                    answerStockTransactions(userId, request, analysis);
        };
    }

    private RagResponse answerHoldings(
            Long userId,
            RetrievalRequest request,
            PortfolioQueryAnalysis analysis) {

        long totalStart = System.nanoTime();

        String holdingsText;

        if (analysis.symbol() != null) {

            HoldingResult holding =
                    portfolioCalculationService
                            .calculateHolding(userId, analysis.symbol());

            holdingsText = holding.symbol() + ": "
                    + holding.quantity() + " shares";

        } else {

            PortfolioHoldingsResult result =
                    portfolioCalculationService
                            .calculateAllHoldings(userId);

            if (result.holdings().isEmpty()) {
                return new RagResponse(
                        "You don't currently own any stocks in your portfolio.",
                        "rule-based",
                        List.of(), 0, 0.0, 0.0,
                        0, 0, 0, 0, 0, 0, 0, 0,
                        (System.nanoTime() - totalStart) / 1_000_000);
            }

            StringBuilder sb = new StringBuilder();
            for (HoldingResult h : result.holdings()) {
                if (!sb.isEmpty()) sb.append("\n");
                sb.append(h.symbol()).append(": ")
                        .append(h.quantity()).append(" shares");
            }
            holdingsText = sb.toString();
        }

        String prompt = "The user asked: \"" + request.question() + "\"\n\n"
                + "Here are the calculated portfolio holdings:\n\n"
                + holdingsText + "\n\n"
                + "Present this information clearly and concisely.";

        long llmStart = System.nanoTime();
        LlmResponse llm = llmService.generate(prompt);
        long llmTime = System.nanoTime() - llmStart;

        long totalTime = System.nanoTime() - totalStart;

        log.info(
                "RAG holdings model={} answerChars={} llmTime={}ms "
                        + "totalTime={}ms",
                llm.model(),
                llm.answer().length(),
                llmTime / 1_000_000,
                totalTime / 1_000_000);

        return new RagResponse(
                llm.answer(),
                llm.model(),
                List.of(),
                0,
                0.0,
                0.0,
                prompt.length(),
                prompt.length() / 4,
                llm.promptTokens(),
                llm.completionTokens(),
                llm.totalTokens(),
                0,
                0,
                llmTime / 1_000_000,
                totalTime / 1_000_000);
    }

    private RagResponse answerStockTransactions(
            Long userId,
            RetrievalRequest request,
            PortfolioQueryAnalysis analysis) {

        List<MetadataFilter> filters = new ArrayList<>();

        if (request.metadataFilters() != null) {
            filters.addAll(request.metadataFilters());
        }

        filters.add(new MetadataFilter(
                MetadataKeys.TYPE,
                FilterOperator.EQ,
                "StockTransaction"));

        if (analysis.symbol() != null) {
            filters.add(new MetadataFilter(
                    MetadataKeys.SYMBOL,
                    FilterOperator.EQ,
                    analysis.symbol()));
        }

        if (analysis.action() != null) {
            filters.add(new MetadataFilter(
                    MetadataKeys.ACTION,
                    FilterOperator.EQ,
                    analysis.action()));
        }

        return answerWithRetrieval(userId, request, filters);
    }

    private RagResponse answerDeposits(
            Long userId,
            RetrievalRequest request,
            PortfolioQueryAnalysis analysis) {

        List<MetadataFilter> filters = new ArrayList<>();

        if (request.metadataFilters() != null) {
            filters.addAll(request.metadataFilters());
        }

        filters.add(new MetadataFilter(
                MetadataKeys.TYPE,
                FilterOperator.EQ,
                "Deposit"));

        return answerWithRetrieval(userId, request, filters);
    }

    private RagResponse answerWithdrawals(
            Long userId,
            RetrievalRequest request,
            PortfolioQueryAnalysis analysis) {

        List<MetadataFilter> filters = new ArrayList<>();

        if (request.metadataFilters() != null) {
            filters.addAll(request.metadataFilters());
        }

        filters.add(new MetadataFilter(
                MetadataKeys.TYPE,
                FilterOperator.EQ,
                "Withdrawal"));

        return answerWithRetrieval(userId, request, filters);
    }

    private RagResponse answerOptionsTransactions(
            Long userId,
            RetrievalRequest request,
            PortfolioQueryAnalysis analysis) {

        List<MetadataFilter> filters = new ArrayList<>();

        if (request.metadataFilters() != null) {
            filters.addAll(request.metadataFilters());
        }

        filters.add(new MetadataFilter(
                MetadataKeys.TYPE,
                FilterOperator.EQ,
                "OptionsTransaction"));

        if (analysis.action() != null) {
            filters.add(new MetadataFilter(
                    MetadataKeys.ACTION,
                    FilterOperator.EQ,
                    analysis.action()));
        }

        if (analysis.symbol() != null) {
            filters.add(new MetadataFilter(
                    MetadataKeys.UNDERLYING_TICKER,
                    FilterOperator.EQ,
                    analysis.symbol()));
        }

        log.debug(
                "answerOptionsTransactions: question='{}' "
                        + "analysis(action={}) filters={}",
                request.question(),
                analysis.action(),
                filters.size());

        return answerWithRetrieval(userId, request, filters);
    }

    private RagResponse answerOptionsPositions(
            Long userId,
            RetrievalRequest request,
            PortfolioQueryAnalysis analysis) {

        List<MetadataFilter> filters = new ArrayList<>();

        if (request.metadataFilters() != null) {
            filters.addAll(request.metadataFilters());
        }

        filters.add(new MetadataFilter(
                MetadataKeys.TYPE,
                FilterOperator.EQ,
                "OptionsPosition"));

        if (analysis.status() != null) {
            filters.add(new MetadataFilter(
                    MetadataKeys.STATUS,
                    FilterOperator.EQ,
                    analysis.status()));
        }

        if (analysis.contractType() != null) {
            filters.add(new MetadataFilter(
                    MetadataKeys.CONTRACT_TYPE,
                    FilterOperator.EQ,
                    analysis.contractType()));
        }

        if (analysis.symbol() != null) {
            filters.add(new MetadataFilter(
                    MetadataKeys.UNDERLYING_TICKER,
                    FilterOperator.EQ,
                    analysis.symbol()));
        }

        log.debug(
                "answerOptionsPositions: question='{}' "
                        + "analysis(status={} contractType={} symbol={}) "
                        + "filters={}",
                request.question(),
                analysis.status(),
                analysis.contractType(),
                analysis.symbol(),
                filters.size());

        return answerWithRetrieval(userId, request, filters);
    }

    private RagResponse answerWithRetrieval(
            Long userId,
            RetrievalRequest request,
            List<MetadataFilter> filters) {

        long totalStart = System.nanoTime();

        RetrievalRequest retrievalRequest = request;

        if (!filters.isEmpty()) {
            retrievalRequest = new RetrievalRequest(
                    request.question(),
                    request.portfolioId(),
                    request.source(),
                    filters);
        }

        log.debug(
                "Portfolio query: question='{}' filters={}",
                request.question(),
                filters.size());

        RetrievalResult retrieval =
                retrievalService.retrieve(userId, retrievalRequest);

        List<SearchResult> results = retrieval.results();

        PromptRequest promptRequest = new PromptRequest(
                request.question(),
                results);

        PromptResult promptResult =
                promptTemplate.build(promptRequest);

        long llmStart = System.nanoTime();
        LlmResponse llm =
                llmService.generate(promptResult.prompt());
        long llmTime = System.nanoTime() - llmStart;

        long totalTime = System.nanoTime() - totalStart;

        List<SearchDocument> sources = results.stream()
                .map(SearchResult::document)
                .toList();

        double averageSimilarity = results.stream()
                .mapToDouble(SearchResult::similarity)
                .average()
                .orElse(0.0);

        double highestSimilarity = results.stream()
                .mapToDouble(SearchResult::similarity)
                .max()
                .orElse(0.0);

        if (log.isDebugEnabled()) {
            log.debug("RAG question: {}", request.question());
            log.debug("RAG prompt:\n{}", promptResult.prompt());
            log.debug("RAG answer:\n{}", llm.answer());
        }

        log.info(
                "RAG {} model={} questionChars={} docs={} promptChars={} "
                        + "promptTokens(est)={} promptTokens(actual)={} "
                        + "completionTokens={} totalTokens={} answerChars={} "
                        + "avgSimilarity={} highestSimilarity={} "
                        + "embeddingTime={}ms retrievalTime={}ms "
                        + "llmTime={}ms totalTime={}ms",
                "summary",
                llm.model(),
                request.question().length(),
                promptResult.contextDocuments(),
                promptResult.promptCharacters(),
                promptResult.estimatedPromptTokens(),
                llm.promptTokens(),
                llm.completionTokens(),
                llm.totalTokens(),
                llm.answer().length(),
                String.format("%.4f", averageSimilarity),
                String.format("%.4f", highestSimilarity),
                retrieval.embeddingTime() / 1_000_000,
                retrieval.retrievalTime() / 1_000_000,
                llmTime / 1_000_000,
                totalTime / 1_000_000);

        return new RagResponse(
                llm.answer(),
                llm.model(),
                sources,
                promptResult.contextDocuments(),
                averageSimilarity,
                highestSimilarity,
                promptResult.promptCharacters(),
                promptResult.estimatedPromptTokens(),
                llm.promptTokens(),
                llm.completionTokens(),
                llm.totalTokens(),
                retrieval.embeddingTime() / 1_000_000,
                retrieval.retrievalTime() / 1_000_000,
                llmTime / 1_000_000,
                totalTime / 1_000_000);
    }

    private RagResponse answerApplicationInfo(
            RetrievalRequest request) {

        long totalStart = System.nanoTime();

        PromptResult promptResult =
                generalPromptTemplate.build(
                        new PromptRequest(
                                request.question(),
                                List.of()));

        long llmStart = System.nanoTime();

        LlmResponse llm =
                llmService.generate(promptResult.prompt());

        long llmTime = System.nanoTime() - llmStart;

        long totalTime = System.nanoTime() - totalStart;

        log.info(
                "RAG application-info model={} promptChars={} "
                        + "promptTokens(est)={} promptTokens(actual)={} "
                        + "completionTokens={} totalTokens={} "
                        + "answerChars={} llmTime={}ms totalTime={}ms",
                llm.model(),
                promptResult.promptCharacters(),
                promptResult.estimatedPromptTokens(),
                llm.promptTokens(),
                llm.completionTokens(),
                llm.totalTokens(),
                llm.answer().length(),
                llmTime / 1_000_000,
                totalTime / 1_000_000);

        return new RagResponse(
                llm.answer(),
                llm.model(),
                List.of(),
                0,
                0.0,
                0.0,
                promptResult.promptCharacters(),
                promptResult.estimatedPromptTokens(),
                llm.promptTokens(),
                llm.completionTokens(),
                llm.totalTokens(),
                0,
                0,
                llmTime / 1_000_000,
                totalTime / 1_000_000);
    }

    private RagResponse answerUnknown(
            RetrievalRequest request) {

        long totalStart = System.nanoTime();

        String answer =
                "I'm not sure how to help with that. "
                        + "You can ask me about your portfolio, "
                        + "stock transactions, deposits, withdrawals, "
                        + "or options positions.";

        long totalTime = System.nanoTime() - totalStart;

        return new RagResponse(
                answer,
                "rule-based",
                List.of(),
                0,
                0.0,
                0.0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                totalTime / 1_000_000);
    }
}
