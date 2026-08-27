package com.rag_system.service.impl;

import com.rag_system.entity.ChatMessage;
import com.rag_system.enums.ChatMessageRole;
import com.rag_system.exception.AppException;
import com.rag_system.repository.ChatMessageRepository;
import com.rag_system.repository.ChatSessionRepository;
import com.rag_system.service.ChatMessageService;
import com.rag_system.service.ChatSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private ChatSessionService chatSessionService;

    @Override
    @Transactional
    public ChatMessage saveMessage(Integer sessionId, ChatMessageRole role, String content, Map<String, Object> uiWidget) {

        ChatMessage message = new ChatMessage();
        message.setSessionId(sessionId);
        message.setRole(role);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        message.setUiWidget(uiWidget);

        ChatMessage saved = chatMessageRepository.save(message);

        chatSessionRepository.findById(sessionId).ifPresent(chatSessionService::touch);

        return saved;
    }

    @Override
    public List<ChatMessage> listMessages(Integer userId, Long sessionId) {

        chatSessionService.getSession(userId, sessionId);

        return chatMessageRepository.findBySessionIdOrderByTimestampAsc(sessionId.intValue());
    }

    @Override
    @Transactional
    public void deleteMessage(Integer userId, Long sessionId, Long messageId) {

        chatSessionService.getSession(userId, sessionId);

        ChatMessage message = chatMessageRepository.findById(messageId.intValue())
                .filter(m -> m.getSessionId().equals(sessionId.intValue()))
                .orElseThrow(() -> new AppException("Message not found", HttpStatus.NOT_FOUND));

        chatMessageRepository.delete(message);
    }

    @Override
    @Transactional
    public void deleteMessages(Integer sessionId) {
        chatMessageRepository.deleteBySessionId(sessionId);
    }
}
