package com.example.kiki.repository;

import com.example.kiki.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Optional<Organization> findByUser_Username(String username);
    boolean existsByOrgName(String orgName);
}
