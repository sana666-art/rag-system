package com.rag_system.repository;

import com.rag_system.entity.SimulatedOptionsPosition;
import com.rag_system.enums.OptionsPositionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimulatedOptionsPositionRepository
        extends JpaRepository<SimulatedOptionsPosition, Integer> {

    List<SimulatedOptionsPosition> findByPortfolioId(
            Integer portfolioId);

    List<SimulatedOptionsPosition> findByStatus(
            OptionsPositionStatus status);

}