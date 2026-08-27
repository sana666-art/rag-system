package com.rag_system.service;

import com.rag_system.dto.retrivalDTO.RetrievalRequest;
import com.rag_system.template.result.RetrievalResult;

public interface RetrievalService {

    RetrievalResult retrieve(
            Long userId,
            RetrievalRequest request);
}
