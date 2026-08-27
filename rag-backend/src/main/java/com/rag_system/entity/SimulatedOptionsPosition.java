package com.rag_system.entity;

import com.rag_system.enums.OptionsPositionSide;
import com.rag_system.enums.OptionsPositionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"SimulatedOptionsPosition\"")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulatedOptionsPosition {

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

    @Column(name = "\"optionTicker\"", nullable = false)
    private String optionTicker;

    @Column(name = "\"underlyingTicker\"", nullable = false)
    private String underlyingTicker;

    @Column(name = "\"contractType\"", nullable = false)
    private String contractType;

    @Column(name = "\"strikePrice\"", nullable = false)
    private BigDecimal strikePrice;

    @Column(name = "\"expirationDate\"", nullable = false)
    private LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OptionsPositionSide side;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "\"avgCostPerContract\"", nullable = false)
    private BigDecimal avgCostPerContract;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OptionsPositionStatus status;

    @Column(name = "\"openedAt\"", nullable = false)
    private LocalDateTime openedAt;

    @Column(name = "\"closedAt\"")
    private LocalDateTime closedAt;

    @Column(name = "\"closePricePerContract\"")
    private BigDecimal closePricePerContract;

    @Column(name = "\"realizedPnl\"")
    private BigDecimal realizedPnl;

    @Column(name = "\"openFee\"", nullable = false)
    private BigDecimal openFee;

    @Column(name = "\"pnlInStockBasis\"", nullable = false)
    private Boolean pnlInStockBasis;

}