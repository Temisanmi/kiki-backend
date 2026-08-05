package com.example.kiki.service;

import com.example.kiki.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConsolePasswordResetSender implements PasswordResetSender{
    @Override
    public void send(User user, String rawToken) {
        log.info("Password reset link for {} ({}): /reset-password?token={} - valid for 10 minutes",
                user.getUsername(), user.getEmail(), rawToken);
    }
}
