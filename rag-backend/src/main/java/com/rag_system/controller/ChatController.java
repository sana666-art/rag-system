package com.rag_system.controller;

import com.rag_system.dto.chatDTO.request.ChatRequest;
import com.rag_system.dto.chatDTO.request.RenameSessionRequest;
import com.rag_system.dto.chatDTO.response.ChatAskResponse;
import com.rag_system.dto.chatDTO.response.ChatMessageResponse;
import com.rag_system.dto.chatDTO.response.ChatSessionResponse;
import com.rag_system.dto.genericApiResonse.ApiResponse;
import com.rag_system.entity.User;
import com.rag_system.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.Executor;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    private final Executor sseStreamExecutor;

    public ChatController(
            ChatService chatService,
            @Qualifier("sseStreamExecutor") Executor sseStreamExecutor) {
        this.chatService = chatService;
        this.sseStreamExecutor = sseStreamExecutor;
    }

    @PostMapping("/ask")
    public ResponseEntity<ChatAskResponse> ask(
            @Valid @RequestBody ChatRequest request,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        ChatAskResponse response = chatService.ask(user.getId(), request);

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(
            @Valid @RequestBody ChatRequest request,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        SseEmitter emitter = new SseEmitter(300_000L);

        sseStreamExecutor.execute(() ->
                chatService.streamChat(user.getId(), request, emitter));

        return emitter;
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<ChatSessionResponse>> listSessions(
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(chatService.listSessions(user.getId()));
    }

    @GetMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @PathVariable Long sessionId,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(chatService.getMessages(user.getId(), sessionId));
    }

    @GetMapping(value = "/sessions/{sessionId}/export",
            produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> exportSession(
            @PathVariable Long sessionId,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        String markdown = chatService.exportSession(user.getId(), sessionId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"chat-" + sessionId + ".md\"")
                .body(markdown);
    }

    @PatchMapping("/sessions/{sessionId}")
    public ResponseEntity<ChatSessionResponse> renameSession(
            @PathVariable Long sessionId,
            @Valid @RequestBody RenameSessionRequest request,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                chatService.renameSession(user.getId(), sessionId, request));
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<ApiResponse> deleteSession(
            @PathVariable Long sessionId,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        chatService.deleteSession(user.getId(), sessionId);

        return ResponseEntity.ok(ApiResponse.success("Chat session deleted"));
    }

    @DeleteMapping("/sessions/{sessionId}/messages/{messageId}")
    public ResponseEntity<ApiResponse> deleteMessage(
            @PathVariable Long sessionId,
            @PathVariable Long messageId,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        chatService.deleteMessage(user.getId(), sessionId, messageId);

        return ResponseEntity.ok(ApiResponse.success("Message deleted"));
    }
}
