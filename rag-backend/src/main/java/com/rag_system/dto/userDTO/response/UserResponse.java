package com.rag_system.dto.userDTO.response;

import com.rag_system.entity.User;
import com.rag_system.enums.UserRole;
import com.rag_system.enums.UserSubscriptionPlan;
import com.rag_system.enums.UserSubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Integer id;

    private String fullName;

    private String email;

    private UserRole role;

    private Boolean twoFactorEnabled;

    private Boolean isEmailVerified;

    private UserSubscriptionPlan subscriptionPlan;

    private UserSubscriptionStatus subscriptionStatus;

    private LocalDateTime createdAt;

    private Integer remainingQuota;

    private Integer quotaLimit;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .twoFactorEnabled(user.getTwoFactorEnabled())
                .isEmailVerified(user.getIsEmailVerified())
                .subscriptionPlan(user.getSubscriptionPlan())
                .subscriptionStatus(user.getSubscriptionStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
