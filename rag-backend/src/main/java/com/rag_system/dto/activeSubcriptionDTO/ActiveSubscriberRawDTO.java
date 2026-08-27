package com.rag_system.dto.activeSubcriptionDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActiveSubscriberRawDTO {

    private String algorithmName;
    private Long activeSubscribers;
    private String activeSubscriberList;
}