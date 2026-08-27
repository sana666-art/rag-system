package com.rag_system.builder;

import com.rag_system.entity.SimulatedPortfolioDeposit;
import com.rag_system.enums.DocumentSource;
import com.rag_system.template.document.DocumentTemplate;
import com.rag_system.template.data.DepositData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DepositDocumentBuilder
        implements DocumentBuilder<SimulatedPortfolioDeposit, DepositData> {

    private final DocumentTemplate<DepositData> template;

    @Override
    public DocumentSource getDocumentSource() {
        return DocumentSource.SimulatedPortfolioDeposit;
    }

    @Override
    public Long getSourceId(SimulatedPortfolioDeposit deposit) {
        return deposit.getId().longValue();
    }

    @Override
    public Long getUserId(SimulatedPortfolioDeposit deposit) {
        return deposit.getPortfolio().getUserId().longValue();
    }

    @Override
    public Long getPortfolioId(SimulatedPortfolioDeposit deposit) {
        return deposit.getPortfolioId().longValue();
    }

    @Override
    public DepositData toData(SimulatedPortfolioDeposit deposit) {
        return new DepositData(
                deposit.getPortfolioId(),
                deposit.getPortfolio().getName(),
                deposit.getPortfolio().getUserId(),
                deposit.getAmount(),
                deposit.getDepositedAt().toLocalDate(),
                deposit.getNote()
        );
    }

    @Override
    public DocumentTemplate<DepositData> getTemplate() {
        return template;
    }
}
