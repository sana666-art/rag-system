package com.rag_system.service;

import com.rag_system.template.result.LlmResponse;
import reactor.core.publisher.Flux;

public interface LlmService {

    LlmResponse generate(String prompt);

    Flux<String> stream(String prompt);

}
