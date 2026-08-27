package com.rag_system.builder;

import com.rag_system.entity.SimulatedPortfolioWithdrawal;
import com.rag_system.enums.DocumentSource;
import com.rag_system.template.document.DocumentTemplate;
import com.rag_system.template.data.WithdrawalData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WithdrawalDocumentBuilder
        implements DocumentBuilder<SimulatedPortfolioWithdrawal, WithdrawalData> {

    private final DocumentTemplate<WithdrawalData> template;

    @Override
    public DocumentSource getDocumentSource() {
        return DocumentSource.SimulatedPortfolioWithdrawal;
    }

    @Override
    public Long getSourceId(SimulatedPortfolioWithdrawal withdrawal) {
        return withdrawal.getId().longValue();
    }

    @Override
    public Long getUserId(SimulatedPortfolioWithdrawal withdrawal) {
        return withdrawal.getPortfolio().getUserId().longValue();
    }

    @Override
    public Long getPortfolioId(SimulatedPortfolioWithdrawal withdrawal) {
        return withdrawal.getPortfolioId().longValue();
    }

    @Override
    public WithdrawalData toData(SimulatedPortfolioWithdrawal withdrawal) {
        return new WithdrawalData(
                withdrawal.getPortfolioId(),
                withdrawal.getPortfolio().getName(),
                withdrawal.getPortfolio().getUserId(),
                withdrawal.getAmount(),
                withdrawal.getWithdrawnAt().toLocalDate(),
                withdrawal.getNote()
        );
    }

    @Override
    public DocumentTemplate<WithdrawalData> getTemplate() {
        return template;
    }
}
