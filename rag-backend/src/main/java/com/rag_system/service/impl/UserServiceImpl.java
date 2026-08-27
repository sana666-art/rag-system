package com.rag_system.service.impl;

import com.rag_system.repository.UserRepository;
import com.rag_system.entity.User;
import com.rag_system.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void toggle2FA(User user) {
        user.setTwoFactorEnabled(user.getTwoFactorEnabled() != true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}
