package com.example.kiki.security;

import com.example.kiki.entity.User;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserPrincipal customPrincipal) {
            return customPrincipal.getUser();
        }
        throw new AuthenticationCredentialsNotFoundException("No authenticated user found");
    }
}