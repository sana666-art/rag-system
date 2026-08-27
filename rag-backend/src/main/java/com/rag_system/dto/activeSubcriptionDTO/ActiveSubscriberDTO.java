package com.rag_system.dto.activeSubcriptionDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ActiveSubscriberDTO {

    private String algorithmName;
    private Long activeSubscribers;
    private List<SubscriberDTO> activeSubscriberList;

}