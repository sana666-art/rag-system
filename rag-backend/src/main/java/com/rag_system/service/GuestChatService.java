package com.rag_system.service;

import com.rag_system.dto.chatDTO.request.GuestAskRequest;
import com.rag_system.dto.chatDTO.response.GuestAskResponse;

public interface GuestChatService {

    GuestAskResponse ask(GuestAskRequest request);
}
