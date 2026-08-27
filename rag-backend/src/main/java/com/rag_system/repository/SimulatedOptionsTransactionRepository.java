package com.rag_system.repository;

import com.rag_system.entity.SimulatedOptionsTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SimulatedOptionsTransactionRepository
        extends JpaRepository<SimulatedOptionsTransaction, Integer> {

    List<SimulatedOptionsTransaction> findByPortfolioId(
            Integer portfolioId);

    List<SimulatedOptionsTransaction> findByPositionId(
            Integer positionId);

    List<SimulatedOptionsTransaction> findByExecutedAtAfter(
            LocalDateTime executedAt);

}