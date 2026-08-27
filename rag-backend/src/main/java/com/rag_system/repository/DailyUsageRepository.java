package com.rag_system.repository;

import com.rag_system.entity.DailyUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailyUsageRepository extends JpaRepository<DailyUsage, Integer> {

    Optional<DailyUsage> findByUserIdAndDate(Integer userId, LocalDate date);

    @Modifying
    @Query(value = "INSERT INTO \"DailyUsage\" (\"userId\", \"date\", \"count\", \"createdAt\", \"updatedAt\") "
            + "VALUES (:userId, :date, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) "
            + "ON CONFLICT (\"userId\", \"date\") "
            + "DO UPDATE SET \"count\" = \"DailyUsage\".\"count\" + 1, \"updatedAt\" = CURRENT_TIMESTAMP",
            nativeQuery = true)
    void incrementUsage(@Param("userId") Integer userId, @Param("date") LocalDate date);
}
