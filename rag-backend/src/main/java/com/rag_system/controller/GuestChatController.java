package com.rag_system.controller;

import com.rag_system.dto.chatDTO.request.GuestAskRequest;
import com.rag_system.dto.chatDTO.response.GuestAskResponse;
import com.rag_system.entity.User;
import com.rag_system.exception.AppException;
import com.rag_system.service.GuestChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guest")
@RequiredArgsConstructor
public class GuestChatController {

    private final GuestChatService guestChatService;

    @PostMapping("/ask")
    public ResponseEntity<GuestAskResponse> ask(
            @Valid @RequestBody GuestAskRequest request,
            Authentication authentication) {

        if (authentication != null && authentication.getPrincipal() instanceof User) {
            throw new AppException(
                    "Guest mode is for anonymous users only. Please log out to continue.",
                    HttpStatus.FORBIDDEN);
        }

        return ResponseEntity.ok(guestChatService.ask(request));
    }
}