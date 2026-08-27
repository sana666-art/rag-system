package com.rag_system.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rag_system.dto.activeSubcriptionDTO.ActiveSubscriberDTO;
import com.rag_system.dto.activeSubcriptionDTO.ActiveSubscriberRawDTO;
import com.rag_system.dto.activeSubcriptionDTO.SubscriberDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@AllArgsConstructor
public class ActiveSubscriberMapper {

    private final ObjectMapper objectMapper;

    public ActiveSubscriberDTO toDto(ActiveSubscriberRawDTO raw) {

        List<SubscriberDTO> subscribers = Collections.emptyList();

        try {
            if (raw.getActiveSubscriberList() != null) {
                subscribers = objectMapper.readValue(
                        raw.getActiveSubscriberList(),
                        new TypeReference<>() {
                        }
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse subscriber list", e);
        }

        return new ActiveSubscriberDTO(
                raw.getAlgorithmName(),
                raw.getActiveSubscribers(),
                subscribers
        );
    }
}