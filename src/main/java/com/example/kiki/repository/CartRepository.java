package com.example.kiki.repository;

import com.example.kiki.entity.Cart;
import com.example.kiki.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long>{
    Optional<Cart> findByUser(User user);
    Optional<Cart> findByUserId(Long userId);
}
