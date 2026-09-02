package com.example.kiki.repository;

import com.example.kiki.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);

    @EntityGraph(attributePaths = "organization")
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @EntityGraph(attributePaths = "organization")
    List<Product> findByOrganization_Id(Long organizationId);

    @Query("SELECT p FROM Product p LEFT JOIN p.organization o " +
            "WHERE p.organization IS NULL OR o.verified = true")
    Page<Product> findAllVisible(Pageable pageable);

    @Query("SELECT p FROM Product p LEFT JOIN p.organization o " +
            "WHERE (p.organization IS NULL OR o.verified = true) " +
            "AND LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> searchVisible(@Param("keyword") String keyword, Pageable pageable);
}