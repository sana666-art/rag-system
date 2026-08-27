package com.rag_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"SimulatedPortfolioDeposit\"")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulatedPortfolioDeposit {

    @Id
    @Column(name = "id")
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

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "\"depositedAt\"", nullable = false)
    private LocalDateTime depositedAt;

    private String note;

}