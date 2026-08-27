package com.rag_system.entity;

import com.rag_system.enums.ChatMessageRole;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Entity
@Table(name = "\"ChatMessage\"")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "\"sessionId\"")
    private Integer sessionId;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private ChatMessageRole role;

    @Column(name = "content")
    private String content;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "type")
    private String type;

    @Column(name = "attachment")
    private String attachment;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "\"uiWidget\"", columnDefinition = "jsonb")
    private Map<String, Object> uiWidget;
}
