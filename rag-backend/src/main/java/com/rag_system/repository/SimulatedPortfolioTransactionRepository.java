package com.rag_system.repository;

import com.rag_system.entity.SimulatedPortfolioTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SimulatedPortfolioTransactionRepository
        extends JpaRepository<SimulatedPortfolioTransaction, Integer> {

    List<SimulatedPortfolioTransaction> findByPortfolioId(
            Integer portfolioId);

    List<SimulatedPortfolioTransaction> findByExecutedAtAfter(
            LocalDateTime executedAt);

    @Query(value = """
            SELECT t.symbol,
                   SUM(CASE WHEN t.type = 'BUY' THEN t.quantity ELSE 0 END)
                 - SUM(CASE WHEN t.type = 'SELL' THEN t.quantity ELSE 0 END) AS quantity
            FROM "SimulatedPortfolioTransaction" t
            JOIN "SimulatedPortfolio" p ON t."portfolioId" = p.id
            WHERE p."userId" = :userId
            GROUP BY t.symbol
            HAVING SUM(CASE WHEN t.type = 'BUY' THEN t.quantity ELSE 0 END)
                 - SUM(CASE WHEN t.type = 'SELL' THEN t.quantity ELSE 0 END) > 0
            """, nativeQuery = true)
    List<Object[]> calculateHoldingsByUserId(@Param("userId") Long userId);

    @Query(value = """
            SELECT t.symbol,
                   SUM(CASE WHEN t.type = 'BUY' THEN t.quantity ELSE 0 END)
                 - SUM(CASE WHEN t.type = 'SELL' THEN t.quantity ELSE 0 END) AS quantity
            FROM "SimulatedPortfolioTransaction" t
            JOIN "SimulatedPortfolio" p ON t."portfolioId" = p.id
            WHERE p."userId" = :userId
              AND t.symbol = :symbol
            GROUP BY t.symbol
            """, nativeQuery = true)
    List<Object[]> calculateHoldingBySymbol(
            @Param("userId") Long userId,
            @Param("symbol") String symbol);
}
