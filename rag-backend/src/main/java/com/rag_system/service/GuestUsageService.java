package com.rag_system.service;

public interface GuestUsageService {

    void assertGuestAvailable(String guestId);

    void consumeGuest(String guestId);

    int remainingGuestQuota(String guestId);

    int guestDailyLimit();
}
