package com.rag_system.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "\"Algorithm\"")
public class Algorithm {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "\"userId\"")
    private Integer userId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "\"strategyType\"")
    private String strategyType;

    @Column(name = "timeframe")
    private String timeframe;

    @Column(name = "\"specificStocks\"")
    private String specificStocks;

    @Type(JsonType.class)
    @Column(name = "conditions", columnDefinition = "jsonb")
    private String conditions;

    @Column(name = "tp")
    private Double tp;

    @Column(name = "sl")
    private Double sl;

    @Column(name = "\"timeExit\"")
    private String timeExit;

    @Column(name = "capital")
    private Double capital;

    @Column(name = "\"minTradeAmount\"")
    private Double minTradeAmount;

    @Column(name = "\"maxTrades\"")
    private Double maxTrades;

    @Column(name = "\"riskPerTrade\"")
    private Double riskPerTrade;

    @Column(name = "\"isPublic\"")
    private Boolean isPublic;

    @Column(name = "\"isMonetized\"")
    private Boolean isMonetized;

    @Column(name = "price")
    private Double price;

    @Column(name = "\"winRate\"")
    private Double winRate;

    @Column(name = "\"totalReturn\"")
    private Double totalReturn;

    @Column(name = "\"maxDrawdown\"")
    private Double maxDrawdown;

    @Column(name = "\"subscriberCount\"")
    private Integer subscriberCount;


    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "\"conditionsLogic\"")
    private String conditionsLogic;

    @Column(name = "\"isVerified\"")
    private Boolean isVerified;

    // PostgreSQL Enum (AlgorithmRunStatus)
    @Column(name = "\"runStatus\"")
    private String runStatus;

    @Column(name = "\"currentVersionId\"")
    private Integer currentVersionId;

    @Column(name = "\"stripeProductId\"")
    private String stripeProductId;

    @Column(name = "\"stripePriceId\"")
    private String stripePriceId;

    @Column(name = "\"tpEnabled\"")
    private Boolean tpEnabled;

    @Column(name = "\"slEnabled\"")
    private Boolean slEnabled;
}