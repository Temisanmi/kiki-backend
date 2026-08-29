package com.example.kiki.service;

import com.example.kiki.dto.auth.AuthResponse;
import com.example.kiki.dto.auth.LoginRequest;
import com.example.kiki.dto.auth.RegisterRequest;
import com.example.kiki.entity.Cart;
import com.example.kiki.entity.User;
import com.example.kiki.exception.DuplicateResourceException;
import com.example.kiki.repository.CartRepository;
import com.example.kiki.repository.OrganizationRepository;
import com.example.kiki.repository.UserRepository;
import com.example.kiki.security.CustomUserPrincipal;
import com.example.kiki.security.JwtUtil;
import com.example.kiki.security.RateLimiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RateLimiterService rateLimiterService;

    public AuthResponse registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(savedUser);
        cartRepository.save(cart);

        String token = jwtUtil.generateToken(savedUser.getUsername());

        return new AuthResponse(
                token,
                savedUser.getUsername(),
                savedUser.getRole().name(),
                null
        );
    }

    public AuthResponse login(LoginRequest request) {
        String limiterKey = "login:" + request.getUsername();
        rateLimiterService.assertNotBlocked(limiterKey);

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException ex) {
            rateLimiterService.recordAttempt(limiterKey, 5, Duration.ofMinutes(10), Duration.ofMinutes(10));
            throw ex;
        }

        rateLimiterService.reset(limiterKey);

        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        User user = principal.getUser();

        String token = jwtUtil.generateToken(user.getUsername());

        Long organizationId = organizationRepository.findByUser_Username(user.getUsername())
                .map(org -> org.getId())
                .orElse(null);

        return new AuthResponse(token, user.getUsername(), user.getRole().name(), organizationId);
    }
}