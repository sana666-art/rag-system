package com.rag_system.repository;

import com.rag_system.entity.SimulatedPortfolioDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SimulatedPortfolioDepositRepository
        extends JpaRepository<SimulatedPortfolioDeposit, Integer> {

    List<SimulatedPortfolioDeposit> findByPortfolioId(Integer portfolioId);

    List<SimulatedPortfolioDeposit> findByDepositedAtAfter(
            LocalDateTime depositedAt);

}