package com.rag_system.repository;

import com.rag_system.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Integer> {

    Optional<ChatSession> findByIdAndUserId(Integer id, Integer userId);

    List<ChatSession> findByUserIdOrderByCreatedAtDesc(Integer userId);
}
