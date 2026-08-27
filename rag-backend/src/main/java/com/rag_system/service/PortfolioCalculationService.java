package com.rag_system.service;

import com.rag_system.dto.ragDTO.HoldingResult;
import com.rag_system.dto.ragDTO.PortfolioHoldingsResult;
import com.rag_system.repository.SimulatedPortfolioTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioCalculationService {

    private static final Logger log =
            LoggerFactory.getLogger(PortfolioCalculationService.class);

    private final SimulatedPortfolioTransactionRepository transactionRepository;

    public PortfolioHoldingsResult calculateAllHoldings(Long userId) {

        List<Object[]> rows =
                transactionRepository.calculateHoldingsByUserId(userId);

        List<HoldingResult> holdings = new ArrayList<>();

        for (Object[] row : rows) {
            String symbol = (String) row[0];
            BigDecimal quantity = new BigDecimal(row[1].toString());
            holdings.add(new HoldingResult(symbol, quantity));
        }

        log.debug(
                "Calculated holdings for userId={}: {} symbols",
                userId,
                holdings.size());

        return new PortfolioHoldingsResult(holdings);
    }

    public HoldingResult calculateHolding(Long userId, String symbol) {

        List<Object[]> rows =
                transactionRepository.calculateHoldingBySymbol(userId, symbol);

        if (rows.isEmpty()) {
            return new HoldingResult(symbol, BigDecimal.ZERO);
        }

        Object[] row = rows.getFirst();
        BigDecimal quantity = new BigDecimal(row[1].toString());

        log.debug(
                "Calculated holding for userId={} symbol={}: {}",
                userId,
                symbol,
                quantity);

        return new HoldingResult(symbol, quantity);
    }
}
