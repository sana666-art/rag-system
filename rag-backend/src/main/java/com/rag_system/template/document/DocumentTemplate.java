package com.rag_system.template.document;

import com.rag_system.template.result.DocumentResult;

public interface DocumentTemplate<D> {

    DocumentResult build(D data);

}
