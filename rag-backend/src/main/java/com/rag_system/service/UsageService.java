package com.rag_system.service;

public interface UsageService {

    void assertAvailable(Integer userId);

    void consume(Integer userId);

    /**
     * Remaining daily quota for the user. Returns -1 for unlimited plans.
     */
    int remainingQuota(Integer userId);

    /**
     * Daily quota limit for the user. Returns -1 for unlimited plans.
     */
    int dailyLimit(Integer userId);
}
