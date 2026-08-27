package com.rag_system.repository;

import com.rag_system.entity.SimulatedPortfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimulatedPortfolioRepository
        extends JpaRepository<SimulatedPortfolio, Integer> {

    List<SimulatedPortfolio> findByUserId(Integer userId);

}