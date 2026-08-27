package com.rag_system.repository;

import com.rag_system.entity.PortfolioDocument;
import com.rag_system.enums.DocumentSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PortfolioDocumentRepository
        extends JpaRepository<PortfolioDocument, Long> {

    boolean existsByDocumentSourceAndSourceId(
            DocumentSource documentSource,
            Long sourceId
    );

    Optional<PortfolioDocument> findByDocumentSourceAndSourceId(
            DocumentSource documentSource,
            Long sourceId
    );

}