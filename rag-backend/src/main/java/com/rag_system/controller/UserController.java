package com.rag_system.controller;

import com.rag_system.dto.genericApiResonse.ApiResponse;
import com.rag_system.dto.userDTO.response.UserResponse;
import com.rag_system.entity.User;
import com.rag_system.service.UsageService;
import com.rag_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UsageService usageService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        UserResponse response = UserResponse.from(user);

        response.setRemainingQuota(usageService.remainingQuota(user.getId()));
        response.setQuotaLimit(usageService.dailyLimit(user.getId()));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/toggle-2fa")
    public ResponseEntity<ApiResponse> toggle2FA(Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        userService.toggle2FA(user);

        if (Boolean.TRUE.equals(user.getTwoFactorEnabled())) {
            return ResponseEntity.ok(ApiResponse.success("Two-factor authentication enabled."));
        } else {
            return ResponseEntity.ok(ApiResponse.success("Two-factor authentication disabled."));
        }
    }
}
