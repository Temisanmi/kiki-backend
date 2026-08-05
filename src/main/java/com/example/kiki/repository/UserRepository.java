package com.example.kiki.repository;

import com.example.kiki.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByResetTokenHash(String resetTokenHash);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
