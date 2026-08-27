package com.rag_system.repository;

import com.rag_system.entity.Otp;
import com.rag_system.enums.OtpPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Integer> {

    Optional<Otp> findByEmailAndPurposeAndVerifiedFalse(String email, OtpPurpose purpose);

    List<Otp> findByEmailAndPurpose(String email, OtpPurpose purpose);

    List<Otp> findByPurposeAndVerifiedFalse(OtpPurpose purpose);

    Optional<Otp> findTopByEmailAndPurposeOrderByCreatedAtDesc(
            String email,
            OtpPurpose purpose
    );

    void deleteByEmailAndPurpose(
            String email,
            OtpPurpose purpose
    );
}
