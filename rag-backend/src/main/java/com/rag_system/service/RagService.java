package com.rag_system.service;

import com.rag_system.dto.ragDTO.RagResponse;
import com.rag_system.dto.retrivalDTO.RetrievalRequest;

public interface RagService {

    RagResponse ask(Long userId, RetrievalRequest request);

}
