package com.example.kiki.controller;

import com.example.kiki.dto.auth.AuthResponse;
import com.example.kiki.dto.auth.RegisterOrganizationRequest;
import com.example.kiki.dto.organization.OrganizationResponseDto;
import com.example.kiki.dto.organization.UpdateOrganizationRequest;
import com.example.kiki.dto.product.ProductResponseDto;
import com.example.kiki.service.OrganizationService;
import com.example.kiki.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {
    private final OrganizationService organizationService;
    private final ProductService productService;

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

    @GetMapping("/me/products")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public ResponseEntity<List<ProductResponseDto>> getMyProducts() {
        return ResponseEntity.ok(productService.getMyProducts());
    }

    @PatchMapping("/{id}/verify") //(?verifed=false)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrganizationResponseDto> setVerified(
            @PathVariable Long id,
            @RequestParam(defaultValue = "true") boolean verified) {
        return ResponseEntity.ok(organizationService.setVerified(id, verified));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrganization(@PathVariable Long id) {
        organizationService.deleteOrganizationById(id);
        return ResponseEntity.noContent().build();
    }
}