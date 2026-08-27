package com.rag_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"SimulatedPortfolio\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimulatedPortfolio {

    @Id
    private Integer id;

    @Column(name = "\"userId\"", nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private String name;

    @Column(name = "\"initialCapital\"")
    private BigDecimal initialCapital;

    @Column(name = "\"cashBalance\"")
    private BigDecimal cashBalance;

    private String color;

    @Column(name = "\"createdAt\"")
    private LocalDateTime createdAt;

    @Column(name = "\"updatedAt\"")
    private LocalDateTime updatedAt;

}