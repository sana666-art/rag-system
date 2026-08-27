package com.rag_system.service;

import com.rag_system.entity.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    void toggle2FA(User user);

}
