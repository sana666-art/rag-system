package com.rag_system.service.impl;

import com.rag_system.entity.GuestDailyUsage;
import com.rag_system.exception.AppException;
import com.rag_system.repository.GuestDailyUsageRepository;
import com.rag_system.service.GuestUsageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class GuestUsageServiceImpl implements GuestUsageService {

    @Value("${rag.guest.daily-limit:5}")
    private int guestDailyLimit;

    @Autowired
    private GuestDailyUsageRepository guestDailyUsageRepository;

    @Override
    public void assertGuestAvailable(String guestId) {

        if (remainingGuestQuota(guestId) <= 0) {
            throw new AppException(
                    "You've reached the free guest limit. Create a free account to continue chatting.",
                    HttpStatus.TOO_MANY_REQUESTS);
        }
    }

    @Override
    @Transactional
    public void consumeGuest(String guestId) {
        guestDailyUsageRepository.incrementUsage(guestId, LocalDate.now());
    }

    @Override
    public int remainingGuestQuota(String guestId) {

        GuestDailyUsage usage = guestDailyUsageRepository
                .findByGuestIdAndDate(guestId, LocalDate.now())
                .orElse(null);

        int used = usage != null ? usage.getCount() : 0;

        return Math.max(0, guestDailyLimit - used);
    }

    @Override
    public int guestDailyLimit() {
        return guestDailyLimit;
    }
}
