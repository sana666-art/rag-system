package com.rag_system.entity;

import com.rag_system.enums.ConnectAccountStatus;
import com.rag_system.enums.UserRole;
import com.rag_system.enums.UserSubscriptionPlan;
import com.rag_system.enums.UserSubscriptionStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Entity
@Table(name = "\"User\"")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "\"googleId\"")
    private String googleId;

    @Column(name = "\"fullName\"")
    private String fullName;

    @Column(name = "bio")
    private String bio;

    @Column(name = "timezone", nullable = false)
    private String timezone = "UTC";

    @Column(name = "\"aiContext\"",  nullable = false)
    private String aiContext = "";

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "role")
    private UserRole role;

    @Column(name = "\"isEmailVerified\"")
    private Boolean isEmailVerified;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "\"notificationPreferences\"", columnDefinition = "jsonb")
    private Map<String, Object> notificationPreferences;

    @Column(name = "watchlist")
    private String[] watchlist;

    @Column(name = "\"createdAt\"")
    private LocalDateTime createdAt;

    @Column(name = "\"updatedAt\"")
    private LocalDateTime updatedAt;

    @Column(name = "\"twoFactorEmail\"")
    private String twoFactorEmail;

    @Column(name = "\"twoFactorEnabled\"")
    private Boolean twoFactorEnabled;

    @Column(name = "\"stripeCustomerId\"")
    private String stripeCustomerId;

    @Column(name = "\"stripeSubscriptionId\"")
    private String stripeSubscriptionId;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "\"subscriptionPlan\"")
    private UserSubscriptionPlan subscriptionPlan = UserSubscriptionPlan.FREE;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "\"subscriptionStatus\"")
    private UserSubscriptionStatus subscriptionStatus = UserSubscriptionStatus.FREE;

    @Column(name = "\"subscriptionPriceId\"")
    private String subscriptionPriceId;

    @Column(name = "\"subscriptionCurrentPeriodEnd\"")
    private LocalDateTime subscriptionCurrentPeriodEnd;

    @Column(name = "\"cardBrand\"")
    private String cardBrand;

    @Column(name = "\"cardLast4\"")
    private String cardLast4;

    @Column(name = "\"stripeConnectAccountId\"")
    private String stripeConnectAccountId;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "\"connectAccountStatus\"")
    private ConnectAccountStatus connectAccountStatus = ConnectAccountStatus.NOT_CONNECTED;

    @Column(name = "\"tokenVersion\"")
    private Integer tokenVersion;

    @Column(name = "\"isBlocked\"")
    private Boolean isBlocked;

    @Column(name = "\"isBlockedAt\"")
    private LocalDateTime isBlockedAt;

    @Column(name = "\"isBlockedReason\"")
    private String isBlockedReason;
}
