package com.example.kiki.service;

import com.example.kiki.dto.user.UpdateUserRequest;
import com.example.kiki.dto.user.UserResponseDto;
import com.example.kiki.entity.User;
import com.example.kiki.exception.DuplicateResourceException;
import com.example.kiki.exception.ResourceNotFoundException;
import com.example.kiki.repository.CartRepository;
import com.example.kiki.repository.OrganizationRepository;
import com.example.kiki.repository.UserRepository;
import com.example.kiki.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final OrganizationRepository organizationRepository;
    private final CurrentUserProvider currentUserProvider;

    private UserResponseDto toResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole().name()
        );
    }

    public Page<UserResponseDto> getAllUsers(Pageable pageable){
        return userRepository.findAll(pageable).map(this::toResponseDto);
    }

    public UserResponseDto getCurrentUser(String username) {
        User user = currentUserProvider.getCurrentUser();
        return toResponseDto(user);
    }

    @Transactional
    public UserResponseDto updateCurrentUser(String currentUsername, UpdateUserRequest request) {
        User user = currentUserProvider.getCurrentUser();

        if (!user.getUsername().equals(request.getUsername())
                && userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already taken");
        }
        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());

        return toResponseDto(userRepository.save(user));
    }

    private void deleteUserAndDependencies(User user) {
        cartRepository.findByUser(user).ifPresent(cartRepository::delete);

        if (user.getRole() == User.Role.ORGANIZATION) {
            organizationRepository.findByUser_Username(user.getUsername())
                    .ifPresent(organizationRepository::delete);
        }
        userRepository.delete(user);
    }

    @Transactional
    public void deleteCurrentUser(String username) {
        User user = currentUserProvider.getCurrentUser();
        deleteUserAndDependencies(user);
    }

    @Transactional
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        deleteUserAndDependencies(user);
    }
}