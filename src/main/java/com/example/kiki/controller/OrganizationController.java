package com.example.kiki.controller;

import com.example.kiki.dto.auth.AuthResponse;
import com.example.kiki.dto.auth.RegisterOrganizationRequest;
import com.example.kiki.dto.organization.OrganizationResponseDto;
import com.example.kiki.dto.organization.UpdateOrganizationRequest;
import com.example.kiki.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {
    private final OrganizationService organizationService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterOrganizationRequest request) {
        AuthResponse response = organizationService.registerOrganization(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<OrganizationResponseDto> getMyOrganization() {
        return ResponseEntity.ok(organizationService.getMyOrganization());
    }

    @PutMapping("/me")
    public ResponseEntity<OrganizationResponseDto> updateMyOrganization(
            @Valid @RequestBody UpdateOrganizationRequest request) {
        return ResponseEntity.ok(organizationService.updateMyOrganization(request));
    }

    @PatchMapping("/{id}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrganizationResponseDto> verifyOrganization(@PathVariable Long id) {
        return ResponseEntity.ok(organizationService.verifyOrganization(id));
    }
}
