package com.rag_system.repository;

import com.rag_system.entity.GuestDailyUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface GuestDailyUsageRepository extends JpaRepository<GuestDailyUsage, Integer> {

    Optional<GuestDailyUsage> findByGuestIdAndDate(String guestId, LocalDate date);

    @Modifying
    @Query(value = "INSERT INTO \"GuestDailyUsage\" (\"guestId\", \"date\", \"count\") "
            + "VALUES (:guestId, :date, 1) "
            + "ON CONFLICT (\"guestId\", \"date\") "
            + "DO UPDATE SET \"count\" = \"GuestDailyUsage\".\"count\" + 1",
            nativeQuery = true)
    void incrementUsage(@Param("guestId") String guestId, @Param("date") LocalDate date);
}
