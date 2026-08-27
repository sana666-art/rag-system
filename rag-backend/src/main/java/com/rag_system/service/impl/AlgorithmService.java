package com.rag_system.service.impl;

import com.rag_system.dto.activeSubcriptionDTO.ActiveSubscriberDTO;
import com.rag_system.mapper.ActiveSubscriberMapper;
import com.rag_system.repository.AlgorithmRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AlgorithmService {

    private final AlgorithmRepository algorithmRepository;

    private ActiveSubscriberMapper activeSubscriberMapper;

    public List<ActiveSubscriberDTO> getActiveSubscribers_func() {
        return algorithmRepository.getActiveSubscribers_func()
                .stream()
                .map(activeSubscriberMapper::toDto)
                .toList();
    }
}
