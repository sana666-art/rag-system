package com.rag_system.entity;

import com.rag_system.enums.DocumentSource;
import com.rag_system.type.VectorType;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import com.pgvector.PGvector;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "\"portfolioDocuments\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "\"userId\"", nullable = false)
    private Long userId;

    @Column(name = "\"portfolioId\"")
    private Long portfolioId;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"documentSource\"", nullable = false)
    private DocumentSource documentSource;

    @Column(name = "\"sourceId\"", nullable = false)
    private Long sourceId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Type(VectorType.class)
    @Column(name = "embedding", columnDefinition = "vector(768)")
    private PGvector embedding;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> metadata = Map.of();

    @Column(name = "\"documentVersion\"")
    private Integer documentVersion;

    @Column(name = "\"isDeleted\"")
    private Boolean isDeleted;

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt;

}