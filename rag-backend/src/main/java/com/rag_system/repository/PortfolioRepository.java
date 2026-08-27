package com.rag_system.repository;

import com.rag_system.entity.PortfolioDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioRepository extends JpaRepository<PortfolioDocument, Integer> {

}
