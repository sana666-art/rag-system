package com.rag_system.service;

import com.rag_system.entity.ChatSession;

import java.util.List;

public interface ChatSessionService {

    ChatSession createSession(Integer userId, String firstQuestion);

    ChatSession resolveSession(Integer userId, Long sessionId, String firstQuestion);

    ChatSession getSession(Integer userId, Long sessionId);

    List<ChatSession> listSessions(Integer userId);

    ChatSession renameSession(Integer userId, Long sessionId, String title);

    void deleteSession(Integer userId, Long sessionId);

    void touch(ChatSession session);
}
