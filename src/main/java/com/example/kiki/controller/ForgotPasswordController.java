package com.example.kiki.controller;

import com.example.kiki.dto.auth.ForgotPasswordRequest;
import com.example.kiki.service.PasswordResetService;
import com.example.kiki.dto.auth.ResetPasswordRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ForgotPasswordController {
    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> requestReset(@Valid @RequestBody ForgotPasswordRequest request) {
        String token = passwordResetService.initiateResetAndReturnToken(request.getEmail());

        Map<String, String> response = new HashMap<>();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reset-password/validate")
    public ResponseEntity<Map<String, Boolean>> validateToken(@RequestParam String token) {
        boolean valid = passwordResetService.isTokenValid(token);
        return ResponseEntity.ok(Map.of("valid", valid));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Passwords don't match."));
        }

        boolean success = passwordResetService.completeReset(request.getToken(), request.getPassword());
        if (!success) {
            return ResponseEntity.badRequest().body(Map.of("error", "That reset link is invalid or expired."));
        }
        return ResponseEntity.ok(Map.of("message", "Password reset successful."));
    }
}
