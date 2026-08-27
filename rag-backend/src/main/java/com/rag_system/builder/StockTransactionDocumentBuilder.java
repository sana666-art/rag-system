package com.rag_system.builder;

import com.rag_system.entity.SimulatedPortfolioTransaction;
import com.rag_system.enums.DocumentSource;
import com.rag_system.template.document.DocumentTemplate;
import com.rag_system.template.data.StockTransactionData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockTransactionDocumentBuilder
        implements DocumentBuilder<SimulatedPortfolioTransaction, StockTransactionData> {

    private final DocumentTemplate<StockTransactionData> template;

    @Override
    public DocumentSource getDocumentSource() {
        return DocumentSource.SimulatedPortfolioTransactions;
    }

    @Override
    public Long getSourceId(SimulatedPortfolioTransaction transaction) {
        return transaction.getId().longValue();
    }

    @Override
    public Long getUserId(SimulatedPortfolioTransaction transaction) {
        return transaction.getPortfolio().getUserId().longValue();
    }

    @Override
    public Long getPortfolioId(SimulatedPortfolioTransaction transaction) {
        return transaction.getPortfolioId().longValue();
    }

    @Override
    public StockTransactionData toData(SimulatedPortfolioTransaction transaction) {
        return new StockTransactionData(
                transaction.getPortfolioId(),
                transaction.getPortfolio().getName(),
                transaction.getPortfolio().getUserId(),
                transaction.getType().name(),
                transaction.getSymbol(),
                transaction.getQuantity(),
                transaction.getPrice(),
                transaction.getFee(),
                transaction.getSource().name(),
                transaction.getExecutedAt().toLocalDate(),
                transaction.getNote()
        );
    }

    @Override
    public DocumentTemplate<StockTransactionData> getTemplate() {
        return template;
    }
}
