package com.rag_system.service.impl;

import com.rag_system.dto.MetadataFilter;
import com.rag_system.dto.PromptRequest;
import com.rag_system.dto.SearchDocument;
import com.rag_system.dto.chatDTO.request.ChatRequest;
import com.rag_system.dto.chatDTO.request.RenameSessionRequest;
import com.rag_system.dto.chatDTO.response.ChatAskResponse;
import com.rag_system.dto.chatDTO.response.ChatMessageResponse;
import com.rag_system.dto.chatDTO.response.ChatSessionResponse;
import com.rag_system.dto.ragDTO.RagResponse;
import com.rag_system.dto.retrivalDTO.RetrievalRequest;
import com.rag_system.entity.ChatMessage;
import com.rag_system.entity.ChatSession;
import com.rag_system.enums.ChatMessageRole;
import com.rag_system.enums.QuestionIntent;
import com.rag_system.dto.ragDTO.PortfolioQueryAnalysis;
import com.rag_system.repository.ChatMessageRepository;
import com.rag_system.service.*;
import com.rag_system.template.prompt.GeneralPromptTemplate;
import com.rag_system.template.prompt.PortfolioPromptTemplate;
import com.rag_system.template.result.PromptResult;
import com.rag_system.template.result.RetrievalResult;
import com.rag_system.template.result.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ChatServiceImpl implements ChatService {

    private static final Logger log =
            LoggerFactory.getLogger(ChatServiceImpl.class);

    @Autowired
    private ChatSessionService chatSessionService;

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UsageService usageService;

    @Autowired
    private RagService ragService;

    @Autowired
    private RetrievalService retrievalService;

    @Autowired
    private PortfolioPromptTemplate promptTemplate;

    @Autowired
    private GeneralPromptTemplate generalPromptTemplate;

    @Autowired
    private QuestionClassifier questionClassifier;

    @Autowired
    private PortfolioQueryAnalyzer portfolioQueryAnalyzer;

    @Autowired
    private LlmService llmService;

    @Override
    @Transactional
    public ChatAskResponse ask(Integer userId, ChatRequest request) {

        usageService.assertAvailable(userId);

        ChatSession session = chatSessionService.resolveSession(
                userId, request.sessionId(), request.question());

        ChatMessage userMessage = chatMessageService.saveMessage(
                session.getId(), ChatMessageRole.USER, request.question(), null);

        RetrievalRequest retrievalRequest = new RetrievalRequest(
                request.question(),
                request.portfolioId(),
                request.source(),
                request.metadataFilters());

        RagResponse rag = ragService.ask(userId.longValue(), retrievalRequest);

        Map<String, Object> uiWidget = buildUiWidget(rag);

        ChatMessage assistantMessage = chatMessageService.saveMessage(
                session.getId(), ChatMessageRole.ASSISTANT, rag.answer(), uiWidget);

        usageService.consume(userId);

        return new ChatAskResponse(
                session.getId(),
                userMessage.getId(),
                assistantMessage.getId(),
                rag);
    }

    @Override
    public void streamChat(Integer userId, ChatRequest request, SseEmitter emitter) {

        try {
            usageService.assertAvailable(userId);
        } catch (Exception e) {
            sendErrorAndComplete(emitter, e.getMessage());
            return;
        }

        ChatSession session;
        try {
            session = chatSessionService.resolveSession(
                    userId, request.sessionId(), request.question());
        } catch (Exception e) {
            sendErrorAndComplete(emitter, e.getMessage());
            return;
        }

        final ChatSession finalSession = session;

        ChatMessage userMessage = chatMessageService.saveMessage(
                finalSession.getId(), ChatMessageRole.USER, request.question(), null);

        final Long userIdLong = userId.longValue();

        QuestionIntent intent = questionClassifier.classify(request.question());

        try {
            sendSseEvent(emitter, "session", Map.of(
                    "sessionId", finalSession.getId(),
                    "userMessageId", userMessage.getId()));

            long totalStart = System.nanoTime();

            if (intent == QuestionIntent.GENERAL) {
                streamGeneralChat(request, emitter, finalSession, userMessage,
                        userId, totalStart);
                return;
            }

            if (intent == QuestionIntent.APPLICATION_INFO
                    || intent == QuestionIntent.UNKNOWN) {
                streamNonRetrievalChat(request, emitter, finalSession,
                        userMessage, userId, userIdLong, totalStart);
                return;
            }

            if (intent == QuestionIntent.PORTFOLIO) {
                PortfolioQueryAnalysis analysis =
                        portfolioQueryAnalyzer.analyze(request.question());

                if ("HOLDINGS".equals(analysis.queryType())) {
                    streamNonRetrievalChat(request, emitter, finalSession,
                            userMessage, userId, userIdLong, totalStart);
                    return;
                }
            }

            RetrievalRequest retrievalRequest = new RetrievalRequest(
                    request.question(),
                    request.portfolioId(),
                    request.source(),
                    request.metadataFilters());

            long embeddingStart = System.nanoTime();
            RetrievalResult retrieval = retrievalService.retrieve(userIdLong, retrievalRequest);
            long embeddingTime = (System.nanoTime() - embeddingStart) / 1_000_000;

            List<SearchResult> results = retrieval.results();

            PromptRequest promptRequest = new PromptRequest(
                    request.question(), results);
            PromptResult promptResult = promptTemplate.build(promptRequest);

            sendSseEvent(emitter, "retrieval", Map.of(
                    "contextDocuments", promptResult.contextDocuments(),
                    "sources", sourcesToMaps(results),
                    "embeddingTime", retrieval.embeddingTime() / 1_000_000,
                    "retrievalTime", retrieval.retrievalTime() / 1_000_000));

            streamAnswerAndFinish(emitter, promptResult, results,
                    finalSession, userMessage, userId, totalStart,
                    embeddingTime, retrieval.retrievalTime() / 1_000_000);

        } catch (Exception e) {
            log.error("Streaming setup failed", e);
            sendErrorAndComplete(emitter, e.getMessage());
        }
    }

    private void streamGeneralChat(
            ChatRequest request,
            SseEmitter emitter,
            ChatSession session,
            ChatMessage userMessage,
            Integer userId,
            long totalStart) {

        PromptResult promptResult = generalPromptTemplate.build(
                new PromptRequest(request.question(), List.of()));

        sendSseEvent(emitter, "retrieval", Map.of(
                "contextDocuments", 0,
                "sources", List.of(),
                "general", true,
                "embeddingTime", 0,
                "retrievalTime", 0));

        streamAnswerAndFinish(emitter, promptResult, List.of(),
                session, userMessage, userId, totalStart, 0, 0);
    }

    private void streamNonRetrievalChat(
            ChatRequest request,
            SseEmitter emitter,
            ChatSession session,
            ChatMessage userMessage,
            Integer userId,
            Long userIdLong,
            long totalStart) {

        RetrievalRequest retrievalRequest = new RetrievalRequest(
                request.question(),
                request.portfolioId(),
                request.source(),
                request.metadataFilters());

        RagResponse rag = ragService.ask(userIdLong, retrievalRequest);

        sendSseEvent(emitter, "retrieval", Map.of(
                "contextDocuments", rag.contextDocuments(),
                "sources", List.of(),
                "embeddingTime", rag.embeddingTime(),
                "retrievalTime", rag.retrievalTime()));

        long llmStart = System.nanoTime();

        String answer = rag.answer();
        String[] words = answer.split(" ");

        for (String word : words) {
            if (!sendTokenEvent(emitter, word + " ")) {
                completeEmitter(emitter);
                return;
            }
        }

        long llmTime = (System.nanoTime() - llmStart) / 1_000_000;
        long totalTime = (System.nanoTime() - totalStart) / 1_000_000;

        RagResponse streamedRag = new RagResponse(
                answer, rag.model(),
                rag.sources(), rag.contextDocuments(),
                rag.averageSimilarity(), rag.highestSimilarity(),
                rag.promptCharacters(), rag.estimatedPromptTokens(),
                rag.promptTokens(), rag.completionTokens(),
                rag.totalTokens(),
                rag.embeddingTime(), rag.retrievalTime(),
                llmTime, totalTime);

        completeStreamSuccessfully(
                emitter, session, userMessage, userId, streamedRag);
    }

    private void streamAnswerAndFinish(
            SseEmitter emitter,
            PromptResult promptResult,
            List<SearchResult> results,
            ChatSession session,
            ChatMessage userMessage,
            Integer userId,
            long totalStart,
            long embeddingTime,
            long retrievalTime) {

        long llmStart = System.nanoTime();
        AtomicReference<String> modelRef = new AtomicReference<>("unknown");
        StringBuilder answerBuilder = new StringBuilder();
        AtomicInteger promptTokens = new AtomicInteger();
        AtomicInteger completionTokens = new AtomicInteger();

        llmService.stream(promptResult.prompt())
                .doOnNext(token -> {
                    answerBuilder.append(token);
                    if (!sendTokenEvent(emitter, token)) {
                        throw new SseDisconnectedException();
                    }
                })
                .doOnComplete(() -> {
                    long llmTime = (System.nanoTime() - llmStart) / 1_000_000;
                    long totalTime = (System.nanoTime() - totalStart) / 1_000_000;

                    RagResponse rag = new RagResponse(
                            answerBuilder.toString(), modelRef.get(),
                            results.stream().map(SearchResult::document).toList(),
                            promptResult.contextDocuments(),
                            results.stream().mapToDouble(SearchResult::similarity)
                                    .average().orElse(0.0),
                            results.stream().mapToDouble(SearchResult::similarity)
                                    .max().orElse(0.0),
                            promptResult.promptCharacters(),
                            promptResult.estimatedPromptTokens(),
                            promptTokens.get(), completionTokens.get(),
                            promptTokens.get() + completionTokens.get(),
                            embeddingTime,
                            retrievalTime,
                            llmTime, totalTime);

                    completeStreamSuccessfully(
                            emitter, session, userMessage, userId, rag);
                })
                .subscribe(
                        null,
                        error -> handleStreamError(emitter, error));
    }

    private void completeStreamSuccessfully(
            SseEmitter emitter,
            ChatSession session,
            ChatMessage userMessage,
            Integer userId,
            RagResponse rag) {

        try {
            Map<String, Object> uiWidget = buildUiWidget(rag);

            ChatMessage assistantMessage = chatMessageService.saveMessage(
                    session.getId(), ChatMessageRole.ASSISTANT,
                    rag.answer(), uiWidget);

            try {
                usageService.consume(userId);
            } catch (Exception e) {
                log.error("Usage consume failed", e);
            }

            Map<String, Object> donePayload = new LinkedHashMap<>();
            donePayload.put("sessionId", session.getId());
            donePayload.put("userMessageId", userMessage.getId());
            donePayload.put("assistantMessageId", assistantMessage.getId());
            donePayload.put("rag", ragToMap(rag));

            sendSseEvent(emitter, "done", donePayload);
            emitter.complete();

        } catch (Exception e) {
            log.error("Failed to persist assistant response", e);
            sendErrorAndComplete(emitter,
                    "Failed to save response: " + e.getMessage());
        }
    }

    private void handleStreamError(SseEmitter emitter, Throwable error) {
        if (error instanceof SseDisconnectedException) {
            log.debug("Client disconnected, stopping stream");
            completeEmitter(emitter);
            return;
        }
        log.error("LLM stream error", error);
        String message = error.getMessage() != null
                ? error.getMessage() : "LLM streaming failed";
        sendErrorAndComplete(emitter, message);
    }

    private boolean sendTokenEvent(SseEmitter emitter, String token) {
        return sendSseEvent(emitter, "token", Map.of("token", token));
    }

    private boolean sendSseEvent(SseEmitter emitter, String eventName, Object data) {
        try {
            emitter.send(SseEmitter.event().name(eventName).data(data));
            return true;
        } catch (IOException e) {
            log.warn("Failed to send SSE event '{}': {}", eventName, e.getMessage());
            return false;
        }
    }

    private void sendErrorAndComplete(SseEmitter emitter, String message) {
        try {
            sendSseEvent(emitter, "error", Map.of("message", message));
        } catch (Exception ignored) {}
        completeEmitter(emitter);
    }

    private void completeEmitter(SseEmitter emitter) {
        try {
            emitter.complete();
        } catch (Exception e) {
            log.warn("Failed to complete SSE emitter: {}", e.getMessage());
        }
    }

    private static final class SseDisconnectedException extends RuntimeException {
        SseDisconnectedException() {
            super("Client disconnected");
        }
    }

    private List<Map<String, Object>> sourcesToMaps(List<SearchResult> results) {
        return results.stream().map(sr -> {
            SearchDocument doc = sr.document();
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("rank", doc.rank());
            map.put("similarity", doc.similarity());
            map.put("source", doc.source());
            map.put("sourceId", doc.sourceId());
            map.put("id", doc.id());
            map.put("content", doc.content());
            map.put("metadata", doc.metadata());
            return map;
        }).toList();
    }

    private Map<String, Object> ragToMap(RagResponse rag) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("answer", rag.answer());
        m.put("model", rag.model());
        m.put("sources", rag.sources() == null ? List.of()
                : rag.sources().stream().map(this::sourceToMapFull).toList());
        m.put("contextDocuments", rag.contextDocuments());
        m.put("averageSimilarity", rag.averageSimilarity());
        m.put("highestSimilarity", rag.highestSimilarity());
        m.put("promptCharacters", rag.promptCharacters());
        m.put("estimatedPromptTokens", rag.estimatedPromptTokens());
        m.put("promptTokens", rag.promptTokens());
        m.put("completionTokens", rag.completionTokens());
        m.put("totalTokens", rag.totalTokens());
        m.put("embeddingTime", rag.embeddingTime());
        m.put("retrievalTime", rag.retrievalTime());
        m.put("llmTime", rag.llmTime());
        m.put("totalTime", rag.totalTime());
        return m;
    }

    private Map<String, Object> sourceToMapFull(SearchDocument s) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", s.id());
        m.put("rank", s.rank());
        m.put("similarity", s.similarity());
        m.put("source", s.source());
        m.put("sourceId", s.sourceId());
        m.put("portfolioId", s.portfolioId());
        m.put("userId", s.userId());
        m.put("content", s.content());
        m.put("metadata", s.metadata());
        return m;
    }

    @Override
    public List<ChatSessionResponse> listSessions(Integer userId) {

        List<ChatSession> sessions = chatSessionService.listSessions(userId);

        List<Integer> sessionIds = sessions.stream()
                .map(ChatSession::getId)
                .toList();

        Map<Integer, String> lastMessages = new HashMap<>();
        if (!sessionIds.isEmpty()) {
            List<ChatMessage> latest = chatMessageRepository
                    .findLatestBySessionIds(sessionIds);
            Set<Integer> seen = new HashSet<>();
            for (ChatMessage message : latest) {
                if (seen.add(message.getSessionId())) {
                    lastMessages.put(
                            message.getSessionId(), message.getContent());
                }
            }
        }

        Map<Integer, Long> tokenTotals = new HashMap<>();
        if (!sessionIds.isEmpty()) {
            List<ChatMessage> assistantMessages = chatMessageRepository
                    .findBySessionIdInAndRole(
                            sessionIds, ChatMessageRole.ASSISTANT);
            for (ChatMessage message : assistantMessages) {
                Long tokens = extractTotalTokens(message.getUiWidget());
                if (tokens != null && tokens > 0) {
                    tokenTotals.merge(
                            message.getSessionId(), tokens, Long::sum);
                }
            }
        }

        return sessions.stream()
                .map(session -> toSessionResponse(
                        session,
                        lastMessages.get(session.getId()),
                        tokenTotals.get(session.getId())))
                .toList();
    }

    private Long extractTotalTokens(Map<String, Object> uiWidget) {
        if (uiWidget == null) return null;
        Object tokens = uiWidget.get("tokens");
        if (!(tokens instanceof Map<?, ?> tokenMap)) return null;
        Object total = tokenMap.get("total");
        return total instanceof Number number ? number.longValue() : null;
    }

    @Override
    public List<ChatMessageResponse> getMessages(Integer userId, Long sessionId) {

        return chatMessageService.listMessages(userId, sessionId).stream()
                .map(this::toMessageResponse)
                .toList();
    }

    @Override
    public ChatSessionResponse renameSession(Integer userId, Long sessionId, RenameSessionRequest request) {

        ChatSession session = chatSessionService.renameSession(userId, sessionId, request.title());

        return toSessionResponse(session);
    }

    @Override
    public String exportSession(Integer userId, Long sessionId) {

        ChatSession session = chatSessionService.getSession(userId, sessionId);
        List<ChatMessage> messages =
                chatMessageService.listMessages(userId, sessionId);

        StringBuilder markdown = new StringBuilder();

        String title = session.getTitle() != null && !session.getTitle().isBlank()
                ? session.getTitle() : "Untitled chat";
        markdown.append("# ").append(title).append("\n\n");
        markdown.append("_Exported on ")
                .append(LocalDateTime.now())
                .append("_\n\n---\n\n");

        for (ChatMessage message : messages) {
            if (message.getRole() == ChatMessageRole.USER) {
                markdown.append("## You\n\n");
            } else {
                markdown.append("## Assistant\n\n");
            }
            String content = message.getContent() == null
                    ? "" : message.getContent();
            markdown.append(content).append("\n\n");
        }

        return markdown.toString();
    }

    @Override
    public void deleteSession(Integer userId, Long sessionId) {
        chatSessionService.deleteSession(userId, sessionId);
    }

    @Override
    public void deleteMessage(Integer userId, Long sessionId, Long messageId) {
        chatMessageService.deleteMessage(userId, sessionId, messageId);
    }

    private ChatSessionResponse toSessionResponse(
            ChatSession session, String lastMessage, Long totalTokens) {
        return new ChatSessionResponse(
                session.getId(),
                session.getTitle(),
                session.getCreatedAt(),
                session.getUpdatedAt(),
                lastMessage,
                totalTokens);
    }

    private ChatSessionResponse toSessionResponse(ChatSession session) {
        return toSessionResponse(session, null, null);
    }

    private ChatMessageResponse toMessageResponse(ChatMessage message) {
        return new ChatMessageResponse(
                message.getId(),
                message.getRole(),
                message.getContent(),
                message.getTimestamp(),
                message.getUiWidget());
    }

    private Map<String, Object> buildUiWidget(RagResponse rag) {

        Map<String, Object> tokens = new LinkedHashMap<>();
        tokens.put("prompt", rag.promptTokens());
        tokens.put("completion", rag.completionTokens());
        tokens.put("total", rag.totalTokens());

        Map<String, Object> timings = new LinkedHashMap<>();
        timings.put("embedding", rag.embeddingTime());
        timings.put("retrieval", rag.retrievalTime());
        timings.put("llm", rag.llmTime());
        timings.put("total", rag.totalTime());

        Map<String, Object> retrieval = new LinkedHashMap<>();
        retrieval.put("averageSimilarity", rag.averageSimilarity());
        retrieval.put("highestSimilarity", rag.highestSimilarity());

        List<Map<String, Object>> sources = rag.sources() == null
                ? List.of()
                : rag.sources().stream().map(this::sourceToMap).toList();

        Map<String, Object> uiWidget = new LinkedHashMap<>();
        uiWidget.put("model", rag.model());
        uiWidget.put("tokens", tokens);
        uiWidget.put("timings", timings);
        uiWidget.put("retrieval", retrieval);
        uiWidget.put("sources", sources);
        uiWidget.put("generatedAt", LocalDateTime.now().toString());

        return uiWidget;
    }

    private Map<String, Object> sourceToMap(SearchDocument source) {

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", source.id());
        map.put("rank", source.rank());
        map.put("similarity", source.similarity());
        map.put("source", source.source());
        map.put("sourceId", source.sourceId());
        map.put("portfolioId", source.portfolioId());
        map.put("userId", source.userId());
        map.put("content", source.content());
        map.put("metadata", source.metadata());

        return map;
    }
}
