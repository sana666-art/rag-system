package com.rag_system.service.impl;

import com.rag_system.entity.ChatSession;
import com.rag_system.exception.AppException;
import com.rag_system.repository.ChatSessionRepository;
import com.rag_system.service.ChatSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatSessionServiceImpl implements ChatSessionService {

    private static final String DEFAULT_TITLE = "New Analysis";

    private static final int TITLE_MAX_LENGTH = 60;

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Override
    @Transactional
    public ChatSession createSession(Integer userId, String firstQuestion) {

        ChatSession session = new ChatSession();
        session.setUserId(userId);
        session.setTitle(deriveTitle(firstQuestion));
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());

        return chatSessionRepository.save(session);
    }

    private String deriveTitle(String firstQuestion) {
        if (firstQuestion == null || firstQuestion.isBlank()) {
            return DEFAULT_TITLE;
        }
        String title = firstQuestion.replaceAll("\\s+", " ").trim();
        if (title.length() > TITLE_MAX_LENGTH) {
            title = title.substring(0, TITLE_MAX_LENGTH).trim() + "…";
        }
        return title;
    }

    @Override
    @Transactional
    public ChatSession resolveSession(Integer userId, Long sessionId, String firstQuestion) {

        if (sessionId == null) {
            return createSession(userId, firstQuestion);
        }

        return getSession(userId, sessionId);
    }

    @Override
    public ChatSession getSession(Integer userId, Long sessionId) {

        return chatSessionRepository
                .findByIdAndUserId(sessionId.intValue(), userId)
                .orElseThrow(() -> new AppException("Chat session not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public List<ChatSession> listSessions(Integer userId) {
        return chatSessionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional
    public ChatSession renameSession(Integer userId, Long sessionId, String title) {

        ChatSession session = getSession(userId, sessionId);
        session.setTitle(title);
        session.setUpdatedAt(LocalDateTime.now());

        return chatSessionRepository.save(session);
    }

    @Override
    @Transactional
    public void deleteSession(Integer userId, Long sessionId) {

        ChatSession session = getSession(userId, sessionId);
        chatSessionRepository.delete(session);
    }

    @Override
    @Transactional
    public void touch(ChatSession session) {
        session.setUpdatedAt(LocalDateTime.now());
        chatSessionRepository.save(session);
    }
}
