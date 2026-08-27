package com.rag_system.controller;

import com.rag_system.dto.ragDTO.RagResponse;
import com.rag_system.dto.retrivalDTO.RetrievalRequest;
import com.rag_system.entity.User;
import com.rag_system.service.RagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rag")
@RequiredArgsConstructor
public class RagController {

    private final RagService ragService;

    @PostMapping("/ask")
    public ResponseEntity<RagResponse> ask(
            @Valid @RequestBody RetrievalRequest request,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        RagResponse response =
                ragService.ask(
                        user.getId().longValue(),
                        request);

        return ResponseEntity.ok(response);
    }

}
