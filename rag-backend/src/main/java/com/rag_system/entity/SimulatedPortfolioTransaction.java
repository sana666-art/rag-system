package com.rag_system.entity;

import com.rag_system.enums.SimTransactionSource;
import com.rag_system.enums.SimTransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "\"SimulatedPortfolioTransaction\"")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulatedPortfolioTransaction {

    @Id
    private Integer id;

    @Column(name = "\"portfolioId\"", nullable = false)
    private Integer portfolioId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "\"portfolioId\"",
            referencedColumnName = "id",
            insertable = false,
            updatable = false
    )
    private SimulatedPortfolio portfolio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SimTransactionType type;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "\"executedAt\"", nullable = false)
    private LocalDateTime executedAt;

    @Column(nullable = false)
    private BigDecimal fee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SimTransactionSource source;

    @Column(name = "\"costBasisPerShare\"")
    private BigDecimal costBasisPerShare;

    private String note;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

}