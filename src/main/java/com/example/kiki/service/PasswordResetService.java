package com.example.kiki.service;

import com.example.kiki.entity.User;
import com.example.kiki.exception.ExpiredOrInvalidTokenException;
import com.example.kiki.repository.UserRepository;
import com.example.kiki.security.RateLimiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final UserRepository userRepository;
    private final PasswordResetSender passwordResetSender;
    private final PasswordEncoder passwordEncoder;
    private final RateLimiterService rateLimiterService;

    private static final long TOKEN_VALID_MINUTES = 10;
    private final SecureRandom random = new SecureRandom();

    private String generateToken() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hashed) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
    public String initiateResetAndReturnToken(String email) {
        String key = "reset:" + email;
        rateLimiterService.assertNotBlocked(key);
        rateLimiterService.recordAttempt(key, 3, Duration.ofMinutes(15), Duration.ofMinutes(15));

        return userRepository.findByEmail(email)
                .map(user -> {
                    String rawToken = generateToken();
                    user.setResetTokenHash(hash(rawToken));
                    user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(TOKEN_VALID_MINUTES));
                    userRepository.save(user);
                    passwordResetSender.send(user, rawToken);
                    return rawToken;
                })
                .orElse(null);
    }

    private Optional<User> findUserByToken(String rawToken) {
        return userRepository.findByResetTokenHash(hash(rawToken))
                .filter(user -> user.getResetTokenExpiry() != null
                        && LocalDateTime.now().isBefore(user.getResetTokenExpiry()));
    }

    public boolean isTokenValid(String rawToken){
        return findUserByToken(rawToken).isPresent();
    }

    public void completeReset(String rawToken, String newPassword){
        User user = findUserByToken(rawToken)
                .orElseThrow(() -> new ExpiredOrInvalidTokenException("That reset link is invalid or expired"));
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetTokenHash(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }
}
