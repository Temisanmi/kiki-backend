package com.example.kiki.controller;

import com.example.kiki.dto.analytics.AdminSummaryDto;
import com.example.kiki.dto.order.OrderDetailDto;
import com.example.kiki.dto.organization.OrganizationResponseDto;
import com.example.kiki.dto.user.UserResponseDto;
import com.example.kiki.entity.User;
import com.example.kiki.service.AdminAnalyticsService;
import com.example.kiki.service.CheckoutService;
import com.example.kiki.service.OrganizationService;
import com.example.kiki.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final OrganizationService organizationService;
    private final AdminAnalyticsService adminAnalyticsService;
    private final CheckoutService checkoutService;

    @GetMapping("/users") //?role=
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(
            @RequestParam(required = false) User.Role role, Pageable pageable){
        return ResponseEntity.ok(userService.getAllUsers(role, pageable));
    }

    @GetMapping("/organizations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrganizationResponseDto>> getAllOrganizations(Pageable pageable) {
        return ResponseEntity.ok(organizationService.getAllOrganizations(pageable));
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminSummaryDto> getSummary() {
        return ResponseEntity.ok(adminAnalyticsService.getSummary());
    }

    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderDetailDto>> getAllOrders(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(checkoutService.getAllOrders(pageable));
    }
}