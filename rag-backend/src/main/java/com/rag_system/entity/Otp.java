package com.rag_system.entity;

import com.rag_system.enums.OtpPurpose;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "\"Otp\"")
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "email")
    private String email;

    @Column(name = "\"otpHash\"")
    private String otpHash;

    @Column(name = "\"expiresAt\"")
    private LocalDateTime expiresAt;

    @Column(name = "verified")
    private Boolean verified;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "purpose")
    private OtpPurpose purpose;

    @Column(name = "\"createdAt\"")
    private LocalDateTime createdAt;

    @Column(name = "\"updatedAt\"")
    private LocalDateTime updatedAt;
}
