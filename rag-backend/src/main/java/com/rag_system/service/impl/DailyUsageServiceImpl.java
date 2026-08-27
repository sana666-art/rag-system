package com.rag_system.service.impl;

import com.rag_system.entity.DailyUsage;
import com.rag_system.entity.User;
import com.rag_system.enums.UserSubscriptionPlan;
import com.rag_system.exception.AppException;
import com.rag_system.repository.DailyUsageRepository;
import com.rag_system.repository.UserRepository;
import com.rag_system.service.UsageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class DailyUsageServiceImpl implements UsageService {

    private static final int FREE_DAILY_LIMIT = 100;

    @Autowired
    private DailyUsageRepository dailyUsageRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void assertAvailable(Integer userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new AppException("User not found", HttpStatus.NOT_FOUND));

        if (user.getSubscriptionPlan() == UserSubscriptionPlan.PRO_MONTHLY) {
            return;
        }

        if (remainingQuota(userId) <= 0) {
            throw new AppException(
                    "You've reached today's free limit. Upgrade or try again tomorrow.",
                    HttpStatus.TOO_MANY_REQUESTS);
        }
    }

    @Override
    @Transactional
    public void consume(Integer userId) {
        dailyUsageRepository.incrementUsage(userId, LocalDate.now());
    }

    @Override
    public int remainingQuota(Integer userId) {

        if (isUnlimited(userId)) {
            return -1;
        }

        DailyUsage usage = dailyUsageRepository
                .findByUserIdAndDate(userId, LocalDate.now())
                .orElse(null);

        int used = usage != null ? usage.getCount() : 0;

        return Math.max(0, FREE_DAILY_LIMIT - used);
    }

    @Override
    public int dailyLimit(Integer userId) {
        return isUnlimited(userId) ? -1 : FREE_DAILY_LIMIT;
    }

    private boolean isUnlimited(Integer userId) {

        return userRepository.findById(userId)
                .map(user -> user.getSubscriptionPlan() == UserSubscriptionPlan.PRO_MONTHLY)
                .orElse(false);
    }
}
