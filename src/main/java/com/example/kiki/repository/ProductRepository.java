package com.example.kiki.repository;

import com.example.kiki.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<Product> findByOrganization_Id(Long organizationId);

    @Query("SELECT p FROM Product p WHERE p.organization IS NULL OR p.organization.verified = true")
    Page<Product> findAllVisible(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE (p.organization IS NULL OR p.organization.verified = true) " +
            "AND LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> searchVisible(@Param("keyword") String keyword, Pageable pageable);
}