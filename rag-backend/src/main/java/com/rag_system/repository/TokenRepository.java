package com.rag_system.repository;

import com.rag_system.entity.Token;
import com.rag_system.entity.User;
import com.rag_system.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {

    Optional<Token> findByToken(String token);

    List<Token> findByUserIdAndType(User userId, TokenType type);

    Optional<Token> findByTokenAndType(
            String token,
            TokenType type
    );
}
