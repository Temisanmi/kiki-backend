package com.example.kiki.controller;

import com.example.kiki.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ForgotPasswordController {
    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> requestReset(@RequestBody Map<String, String> body) {
        passwordResetService.initiateReset(body.get("email"));
        return ResponseEntity.ok(Map.of("message",
                "If an account exists for that email, a reset link has been sent."));
    }

    @GetMapping("/reset-password/validate")
    public ResponseEntity<Map<String, Boolean>> validateToken(@RequestParam String token) {
        boolean valid = passwordResetService.isTokenValid(token);
        return ResponseEntity.ok(Map.of("valid", valid));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String password = body.get("password");
        String confirmPassword = body.get("confirmPassword");

        if (!password.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Passwords don't match."));
        }

        boolean success = passwordResetService.completeReset(token, password);
        if (!success) {
            return ResponseEntity.badRequest().body(Map.of("error", "That reset link is invalid or expired."));
        }

        return ResponseEntity.ok(Map.of("message", "Password reset successful."));
    }
}