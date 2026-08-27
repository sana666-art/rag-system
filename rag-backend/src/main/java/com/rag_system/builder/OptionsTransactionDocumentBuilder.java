package com.rag_system.builder;

import com.rag_system.entity.SimulatedOptionsTransaction;
import com.rag_system.enums.DocumentSource;
import com.rag_system.template.document.DocumentTemplate;
import com.rag_system.template.data.OptionsTransactionData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OptionsTransactionDocumentBuilder
        implements DocumentBuilder<SimulatedOptionsTransaction, OptionsTransactionData> {

    private final DocumentTemplate<OptionsTransactionData> template;

    @Override
    public DocumentSource getDocumentSource() {
        return DocumentSource.SimulatedOptionsTransactions;
    }

    @Override
    public Long getSourceId(SimulatedOptionsTransaction transaction) {
        return transaction.getId().longValue();
    }

    @Override
    public Long getUserId(SimulatedOptionsTransaction transaction) {
        return transaction.getPortfolio().getUserId().longValue();
    }

    @Override
    public Long getPortfolioId(SimulatedOptionsTransaction transaction) {
        return transaction.getPortfolioId().longValue();
    }

    @Override
    public OptionsTransactionData toData(SimulatedOptionsTransaction transaction) {
        return new OptionsTransactionData(
                transaction.getPortfolioId(),
                transaction.getPortfolio().getName(),
                transaction.getPortfolio().getUserId(),
                transaction.getAction().name(),
                transaction.getOptionTicker(),
                transaction.getQuantity(),
                transaction.getPricePerContract(),
                transaction.getTotalCost(),
                transaction.getFee(),
                transaction.getRealizedPnl(),
                transaction.getExecutedAt().toLocalDate(),
                transaction.getNote()
        );
    }

    @Override
    public DocumentTemplate<OptionsTransactionData> getTemplate() {
        return template;
    }
}
