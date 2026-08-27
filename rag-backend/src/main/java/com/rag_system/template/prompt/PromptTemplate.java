package com.rag_system.template.prompt;

import com.rag_system.template.result.PromptResult;

public interface PromptTemplate<T> {

    PromptResult build(T request);

}
