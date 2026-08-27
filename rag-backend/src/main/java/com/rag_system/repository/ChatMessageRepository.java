package com.rag_system.repository;

import com.rag_system.entity.ChatMessage;
import com.rag_system.enums.ChatMessageRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {

    List<ChatMessage> findBySessionIdOrderByTimestampAsc(Integer sessionId);

    List<ChatMessage> findBySessionIdInAndRole(
            Collection<Integer> sessionIds, ChatMessageRole role);

    void deleteBySessionId(Integer sessionId);

    @Query("""
            SELECT m FROM ChatMessage m
            WHERE m.sessionId IN :sessionIds
            ORDER BY m.timestamp DESC, m.id DESC
            """)
    List<ChatMessage> findLatestBySessionIds(
            @Param("sessionIds") Collection<Integer> sessionIds);
}
