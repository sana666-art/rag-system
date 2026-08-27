package com.rag_system.template.document;

import com.rag_system.template.data.PortfolioData;
import com.rag_system.template.result.DocumentResult;
import com.rag_system.util.MetadataKeys;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class PortfolioDocumentTemplate implements DocumentTemplate<PortfolioData> {

    @Override
    public DocumentResult build(PortfolioData data) {

        String content = String.format("""
                Portfolio "%s".

                It was created with an initial capital of %s.
                The portfolio currently has a cash balance of %s.
                The portfolio colour is %s.
                It was created on %s.""",
                data.name(),
                data.initialCapital(),
                data.cashBalance(),
                data.color(),
                data.createdAt()
        );

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put(MetadataKeys.TYPE, "Portfolio");
        metadata.put(MetadataKeys.PORTFOLIO_NAME, data.name());
        metadata.put(MetadataKeys.USER_ID, data.userId());

        return new DocumentResult(content, metadata);
    }
}
