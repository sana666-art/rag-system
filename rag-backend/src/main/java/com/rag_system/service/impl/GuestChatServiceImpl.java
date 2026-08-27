package com.rag_system.service.impl;

import com.rag_system.dto.chatDTO.request.GuestAskRequest;
import com.rag_system.dto.chatDTO.response.GuestAskResponse;
import com.rag_system.dto.ragDTO.RagResponse;
import com.rag_system.dto.retrivalDTO.RetrievalRequest;
import com.rag_system.service.GuestChatService;
import com.rag_system.service.GuestUsageService;
import com.rag_system.service.RagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GuestChatServiceImpl implements GuestChatService {

    @Value("${rag.guest.demo-user-id:8}")
    private Long demoUserId;

    @Autowired
    private GuestUsageService guestUsageService;

    @Autowired
    private RagService ragService;

    @Override
    public GuestAskResponse ask(GuestAskRequest request) {

        guestUsageService.assertGuestAvailable(request.guestId());

        RetrievalRequest retrievalRequest = new RetrievalRequest(
                request.question(), null, null, null);

        RagResponse rag = ragService.ask(demoUserId, retrievalRequest);

        guestUsageService.consumeGuest(request.guestId());

        return new GuestAskResponse(
                rag,
                guestUsageService.remainingGuestQuota(request.guestId()),
                guestUsageService.guestDailyLimit());
    }
}
