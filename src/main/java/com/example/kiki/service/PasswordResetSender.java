package com.example.kiki.service;

import com.example.kiki.entity.User;

public interface PasswordResetSender {
    void send(User user, String rawToken);
}
