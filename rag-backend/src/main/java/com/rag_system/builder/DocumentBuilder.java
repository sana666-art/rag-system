package com.rag_system.builder;

import com.rag_system.enums.DocumentSource;
import com.rag_system.template.result.DocumentResult;
import com.rag_system.template.document.DocumentTemplate;

public interface DocumentBuilder<E, D> {

    DocumentSource getDocumentSource();

    Long getSourceId(E entity);

    Long getUserId(E entity);

    Long getPortfolioId(E entity);

    D toData(E entity);

    DocumentTemplate<D> getTemplate();

    default DocumentResult build(E entity) {
        return getTemplate().build(toData(entity));
    }

}
