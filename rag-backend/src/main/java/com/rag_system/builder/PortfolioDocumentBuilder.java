package com.rag_system.builder;

import com.rag_system.entity.SimulatedPortfolio;
import com.rag_system.enums.DocumentSource;
import com.rag_system.template.document.DocumentTemplate;
import com.rag_system.template.data.PortfolioData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PortfolioDocumentBuilder
        implements DocumentBuilder<SimulatedPortfolio, PortfolioData> {

    private final DocumentTemplate<PortfolioData> template;

    @Override
    public DocumentSource getDocumentSource() {
        return DocumentSource.SimulatedPortfolio;
    }

    @Override
    public Long getSourceId(SimulatedPortfolio portfolio) {
        return portfolio.getId().longValue();
    }

    @Override
    public Long getUserId(SimulatedPortfolio portfolio) {
        return portfolio.getUserId().longValue();
    }

    @Override
    public Long getPortfolioId(SimulatedPortfolio portfolio) {
        return portfolio.getId().longValue();
    }

    @Override
    public PortfolioData toData(SimulatedPortfolio portfolio) {
        return new PortfolioData(
                portfolio.getName(),
                portfolio.getUserId(),
                portfolio.getInitialCapital(),
                portfolio.getCashBalance(),
                portfolio.getColor(),
                portfolio.getCreatedAt().toLocalDate()
        );
    }

    @Override
    public DocumentTemplate<PortfolioData> getTemplate() {
        return template;
    }
}
