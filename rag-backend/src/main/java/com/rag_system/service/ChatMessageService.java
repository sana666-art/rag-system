package com.rag_system.service;

import com.rag_system.entity.ChatMessage;
import com.rag_system.enums.ChatMessageRole;

import java.util.List;
import java.util.Map;

public interface ChatMessageService {

    ChatMessage saveMessage(Integer sessionId, ChatMessageRole role, String content, Map<String, Object> uiWidget);

    List<ChatMessage> listMessages(Integer userId, Long sessionId);

    void deleteMessage(Integer userId, Long sessionId, Long messageId);

    void deleteMessages(Integer sessionId);
}
