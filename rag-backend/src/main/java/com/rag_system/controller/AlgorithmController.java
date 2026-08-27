package com.rag_system.controller;

import com.rag_system.dto.activeSubcriptionDTO.ActiveSubscriberDTO;
import com.rag_system.service.impl.AlgorithmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AlgorithmController {

    @Autowired
    private AlgorithmService AlgorithmService;

    @GetMapping("/active-subscribers-using-func")
    public List<ActiveSubscriberDTO> getSubscribers() {
        return AlgorithmService.getActiveSubscribers_func();
    }
}