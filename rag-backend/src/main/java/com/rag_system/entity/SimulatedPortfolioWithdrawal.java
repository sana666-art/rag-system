package com.rag_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"SimulatedPortfolioWithdrawal\"")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulatedPortfolioWithdrawal {

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

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "\"withdrawnAt\"", nullable = false)
    private LocalDateTime withdrawnAt;

    private String note;

}