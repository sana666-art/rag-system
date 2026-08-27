package com.rag_system.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rag_system.exception.LlmException;
import com.rag_system.service.LlmService;
import com.rag_system.template.result.LlmResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * LlmService backed by the Google Gemini REST API
 * (generativelanguage.googleapis.com).
 * Uses a plain API key, so no additional Spring AI provider
 * dependency is required.
 */
@Service
public class GeminiLlmService implements LlmService {

    private static final Logger log =
            LoggerFactory.getLogger(GeminiLlmService.class);

    private static final String BASE_URL =
            "https://generativelanguage.googleapis.com/v1beta";

    private static final String QUOTA_EXCEEDED_MESSAGE =
            "The daily AI answer limit has been reached. Please try again tomorrow.";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${spring.ai.google.gemini.api-key}")
    private String apiKey;

    @Value("${spring.ai.google.gemini.chat.model:gemini-3.6-flash}")
    private String model;

    @Value("${spring.ai.google.gemini.chat.options.temperature:0.2}")
    private double temperature;

    @Value("${spring.ai.google.gemini.chat.options.max-tokens:2048}")
    private int maxTokens;

    @Value("${spring.ai.google.gemini.chat.options.thinking-level:low}")
    private String thinkingLevel;

    public GeminiLlmService(
            RestClient.Builder builder,
            ObjectMapper objectMapper) {

        this.restClient = builder
                .baseUrl(BASE_URL)
                .build();

        this.objectMapper = objectMapper;
    }

    @Override
    public LlmResponse generate(String prompt) {

        try {

            GeminiResponse response =
                    restClient.post()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/models/{model}:generateContent")
                                    .queryParam("key", apiKey)
                                    .build(model))
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(requestBody(prompt))
                            .retrieve()
                            .body(GeminiResponse.class);

            if (response == null
                    || response.candidates() == null
                    || response.candidates().isEmpty()) {

                throw new LlmException(
                        "Gemini returned no candidates",
                        null);
            }

            String answer = extractText(response);

            int promptTokens =
                    response.usageMetadata() != null
                            ? response.usageMetadata().promptTokenCount()
                            : 0;

            int completionTokens =
                    response.usageMetadata() != null
                            ? response.usageMetadata().candidatesTokenCount()
                            : 0;

            int totalTokens =
                    response.usageMetadata() != null
                            ? response.usageMetadata().totalTokenCount()
                            : 0;

            int thoughtsTokens =
                    response.usageMetadata() != null
                            ? response.usageMetadata().thoughtsTokenCount()
                            : 0;

            String responseModel =
                    response.modelVersion() != null
                            ? response.modelVersion()
                            : model;

            String finishReason =
                    response.candidates()
                            .getFirst()
                            .finishReason();

            log.info(
                    "Gemini generation: model={} temperature={} "
                            + "thinkingLevel={} maxOutputTokens={} "
                            + "promptTokens={} thoughtsTokens={} "
                            + "completionTokens={} totalTokens={} "
                            + "finishReason={}",
                    responseModel,
                    temperature,
                    thinkingLevel,
                    maxTokens,
                    promptTokens,
                    thoughtsTokens,
                    completionTokens,
                    totalTokens,
                    finishReason);

            return new LlmResponse(
                    answer,
                    responseModel,
                    promptTokens,
                    completionTokens,
                    totalTokens);

        } catch (HttpClientErrorException.TooManyRequests e) {

            String geminiError = e.getResponseBodyAsString();

            log.warn("Gemini 429 response body: {}", geminiError);

            throw new LlmException(
                    QUOTA_EXCEEDED_MESSAGE,
                    e,
                    HttpStatus.TOO_MANY_REQUESTS);

        } catch (LlmException e) {

            throw e;

        } catch (Exception e) {

            log.error("Gemini call failed", e);

            throw new LlmException(
                    "LLM generation failed: " + e.getMessage(),
                    e);
        }
    }

    @Override
    public Flux<String> stream(String prompt) {

        try {

            return restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/models/{model}:streamGenerateContent")
                            .queryParam("alt", "sse")
                            .queryParam("key", apiKey)
                            .build(model))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody(prompt))
                    .exchange((request, response) -> {

                        if (!response.getStatusCode().is2xxSuccessful()) {

                            try (InputStream errorBody =
                                         response.getBody()) {

                                if (response.getStatusCode()
                                        == HttpStatus.TOO_MANY_REQUESTS) {

                                    String body = readBody(errorBody);

                                    log.warn(
                                            "Gemini 429 stream response body: {}",
                                            body);

                                    throw new LlmException(
                                            QUOTA_EXCEEDED_MESSAGE,
                                            null,
                                            HttpStatus.TOO_MANY_REQUESTS);
                                }

                                throw new LlmException(
                                        "Gemini API error "
                                                + response.getStatusCode()
                                                + ": "
                                                + readBody(errorBody),
                                        null);
                            }
                        }

                        InputStream responseBody =
                                response.getBody();

                        return Flux.create(
                                sink ->
                                        readSseStream(
                                                responseBody,
                                                sink),
                                FluxSink.OverflowStrategy.BUFFER);
                    }, false);

        } catch (Exception e) {

            log.error("Gemini stream failed", e);

            throw new LlmException(
                    "LLM streaming failed: "
                            + e.getMessage(),
                    e);
        }
    }

    private Map<String, Object> requestBody(String prompt) {

        return Map.of(
                "contents",
                List.of(
                        Map.of(
                                "parts",
                                List.of(
                                        Map.of(
                                                "text",
                                                prompt)))),
                "generationConfig",
                Map.of(
                        "temperature",
                        temperature,
                        "maxOutputTokens",
                        maxTokens,
                        "thinkingConfig",
                        Map.of(
                                "thinkingLevel",
                                thinkingLevel)));
    }

    private void readSseStream(
            InputStream body,
            FluxSink<String> sink) {

        StringBuilder dataBuffer =
                new StringBuilder();

        try (
                InputStream stream = body;
                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        stream,
                                        StandardCharsets.UTF_8))
        ) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.startsWith("data:")) {

                    if (!dataBuffer.isEmpty()) {
                        dataBuffer.append('\n');
                    }

                    dataBuffer.append(
                            line.substring(5).trim());

                    continue;
                }

                if (line.isEmpty()
                        && !dataBuffer.isEmpty()) {

                    emitChunk(
                            dataBuffer.toString(),
                            sink);

                    dataBuffer.setLength(0);
                }
            }

            if (!dataBuffer.isEmpty()) {

                emitChunk(
                        dataBuffer.toString(),
                        sink);
            }

            sink.complete();

        } catch (IOException e) {

            log.warn(
                    "Gemini stream ended prematurely: {}",
                    e.getMessage());

            if (!dataBuffer.isEmpty()) {

                emitChunk(
                        dataBuffer.toString(),
                        sink);
            }

            sink.complete();
        }
    }

    private void emitChunk(
            String data,
            FluxSink<String> sink) {

        try {

            GeminiResponse response =
                    parseJson(data);

            String text =
                    extractText(response);

            if (text != null
                    && !text.isEmpty()) {

                sink.next(text);
            }

        } catch (Exception e) {

            log.warn(
                    "Failed to parse Gemini stream chunk",
                    e);
        }
    }

    private String extractText(
            GeminiResponse response) {

        if (response.candidates() == null
                || response.candidates().isEmpty()) {

            return "";
        }

        Content content =
                response.candidates()
                        .getFirst()
                        .content();

        if (content == null
                || content.parts() == null
                || content.parts().isEmpty()) {

            return "";
        }

        return content.parts()
                .getFirst()
                .text();
    }

    private GeminiResponse parseJson(
            String json) throws IOException {

        return objectMapper.readValue(
                json,
                GeminiResponse.class);
    }

    private String readBody(
            InputStream body) {

        if (body == null) {
            return "";
        }

        try {

            return new String(
                    body.readAllBytes(),
                    StandardCharsets.UTF_8);

        } catch (IOException e) {

            return "";
        }
    }

    private record GeminiResponse(
            List<Candidate> candidates,
            UsageMetadata usageMetadata,
            String modelVersion
    ) {}

    private record Candidate(
            Content content,
            String finishReason
    ) {}

    private record Content(
            List<Part> parts
    ) {}

    private record Part(
            String text
    ) {}

    private record UsageMetadata(
            int promptTokenCount,
            int candidatesTokenCount,
            int totalTokenCount,
            int thoughtsTokenCount
    ) {}
}