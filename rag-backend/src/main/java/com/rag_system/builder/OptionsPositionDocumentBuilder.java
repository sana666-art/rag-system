package com.rag_system.builder;

import com.rag_system.entity.SimulatedOptionsPosition;
import com.rag_system.enums.DocumentSource;
import com.rag_system.template.document.DocumentTemplate;
import com.rag_system.template.data.OptionsPositionData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OptionsPositionDocumentBuilder
        implements DocumentBuilder<SimulatedOptionsPosition, OptionsPositionData> {

    private final DocumentTemplate<OptionsPositionData> template;

    @Override
    public DocumentSource getDocumentSource() {
        return DocumentSource.SimulatedOptionsPosition;
    }

    @Override
    public Long getSourceId(SimulatedOptionsPosition position) {
        return position.getId().longValue();
    }

    @Override
    public Long getUserId(SimulatedOptionsPosition position) {
        return position.getPortfolio().getUserId().longValue();
    }

    @Override
    public Long getPortfolioId(SimulatedOptionsPosition position) {
        return position.getPortfolioId().longValue();
    }

    @Override
    public OptionsPositionData toData(SimulatedOptionsPosition position) {
        return new OptionsPositionData(
                position.getPortfolioId(),
                position.getPortfolio().getName(),
                position.getPortfolio().getUserId(),
                position.getOptionTicker(),
                position.getUnderlyingTicker(),
                position.getContractType(),
                position.getStrikePrice(),
                position.getExpirationDate(),
                position.getSide().name(),
                position.getQuantity(),
                position.getAvgCostPerContract(),
                position.getStatus().name(),
                position.getRealizedPnl()
        );
    }

    @Override
    public DocumentTemplate<OptionsPositionData> getTemplate() {
        return template;
    }
}
