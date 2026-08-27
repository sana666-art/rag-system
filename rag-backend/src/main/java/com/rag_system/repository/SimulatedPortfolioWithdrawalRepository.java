package com.rag_system.repository;

import com.rag_system.entity.SimulatedPortfolioWithdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SimulatedPortfolioWithdrawalRepository
        extends JpaRepository<SimulatedPortfolioWithdrawal, Integer> {

    List<SimulatedPortfolioWithdrawal> findByPortfolioId(Integer portfolioId);

    List<SimulatedPortfolioWithdrawal> findByWithdrawnAtAfter(
            LocalDateTime withdrawnAt);

}