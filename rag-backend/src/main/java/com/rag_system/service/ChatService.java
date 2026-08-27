package com.rag_system.service;

import com.rag_system.dto.chatDTO.request.ChatRequest;
import com.rag_system.dto.chatDTO.request.RenameSessionRequest;
import com.rag_system.dto.chatDTO.response.ChatAskResponse;
import com.rag_system.dto.chatDTO.response.ChatMessageResponse;
import com.rag_system.dto.chatDTO.response.ChatSessionResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface ChatService {

    ChatAskResponse ask(Integer userId, ChatRequest request);

    void streamChat(Integer userId, ChatRequest request, SseEmitter emitter);

    List<ChatSessionResponse> listSessions(Integer userId);

    List<ChatMessageResponse> getMessages(Integer userId, Long sessionId);

    String exportSession(Integer userId, Long sessionId);

    ChatSessionResponse renameSession(Integer userId, Long sessionId, RenameSessionRequest request);

    void deleteSession(Integer userId, Long sessionId);

    void deleteMessage(Integer userId, Long sessionId, Long messageId);

}