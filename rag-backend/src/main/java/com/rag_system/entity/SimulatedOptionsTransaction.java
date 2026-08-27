package com.rag_system.entity;

import com.rag_system.enums.OptionsTradeAction;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "\"SimulatedOptionsTransaction\"")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulatedOptionsTransaction {

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

    @Column(name = "\"positionId\"", nullable = false)
    private Integer positionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OptionsTradeAction action;

    @Column(name = "\"optionTicker\"", nullable = false)
    private String optionTicker;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "\"pricePerContract\"", nullable = false)
    private BigDecimal pricePerContract;

    @Column(name = "\"totalCost\"", nullable = false)
    private BigDecimal totalCost;

    @Column(nullable = false)
    private BigDecimal fee;

    @Column(name = "\"executedAt\"", nullable = false)
    private LocalDateTime executedAt;

    @Column(name = "\"realizedPnl\"")
    private BigDecimal realizedPnl;

    private String note;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

}