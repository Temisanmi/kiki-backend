package com.example.kiki.service;

import com.example.kiki.dto.analytics.OrganizationSummaryDto;
import com.example.kiki.dto.analytics.SalesSummaryDto;
import com.example.kiki.dto.analytics.TopProductDto;
import com.example.kiki.dto.auth.AuthResponse;
import com.example.kiki.dto.auth.RegisterOrganizationRequest;
import com.example.kiki.dto.organization.OrganizationResponseDto;
import com.example.kiki.dto.organization.UpdateOrganizationRequest;
import com.example.kiki.entity.Organization;
import com.example.kiki.entity.User;
import com.example.kiki.exception.DuplicateResourceException;
import com.example.kiki.exception.ForbiddenOperationException;
import com.example.kiki.exception.ResourceNotFoundException;
import com.example.kiki.repository.CartActivityRepository;
import com.example.kiki.repository.OrderItemRepository;
import com.example.kiki.repository.OrganizationRepository;
import com.example.kiki.repository.UserRepository;
import com.example.kiki.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationService {
    private final UserRepository userRepository;
    private final CartActivityRepository cartActivityRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse registerOrganization(RegisterOrganizationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }
        if (organizationRepository.existsByOrgName(request.getOrgName())) {
            throw new DuplicateResourceException("Organization name already taken");
        }

        User user = new User();
        user.setFirstName(request.getContactFirstName());
        user.setLastName(request.getContactLastName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.ORGANIZATION);

        User savedUser = userRepository.save(user);

        Organization organization = new Organization();
        organization.setOrgName(request.getOrgName());
        organization.setOrgDescription(request.getOrgDescription());
        organization.setLogoUrl(request.getLogoUrl());
        organization.setVerified(false);
        organization.setUser(savedUser);

        Organization savedOrganization = organizationRepository.save(organization);

        String token = jwtUtil.generateToken(savedUser.getUsername());

        return new AuthResponse(
                token,
                savedUser.getUsername(),
                savedUser.getRole().name(),
                savedOrganization.getId()
        );
    }

    private OrganizationResponseDto toResponseDto(Organization organization) {
        return new OrganizationResponseDto(
                organization.getId(),
                organization.getOrgName(),
                organization.getOrgDescription(),
                organization.getLogoUrl(),
                organization.isVerified()
        );
    }

    private Organization getCurrentOrganization() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return organizationRepository.findByUser_Username(username)
                .orElseThrow(() -> new ForbiddenOperationException("No organization profile found for this account"));
    }

    public Page<OrganizationResponseDto> getAllOrganizations(Pageable pageable) {
        return organizationRepository.findAll(pageable).map(this::toResponseDto);
    }

    public OrganizationResponseDto getMyOrganization() {
        return toResponseDto(getCurrentOrganization());
    }

    @Transactional
    public OrganizationResponseDto updateMyOrganization(UpdateOrganizationRequest request) {
        Organization organization = getCurrentOrganization();

        if (!organization.getOrgName().equals(request.getOrgName())
                && organizationRepository.existsByOrgName(request.getOrgName())) {
            throw new DuplicateResourceException("Organization name already taken");
        }

        organization.setOrgName(request.getOrgName());
        organization.setOrgDescription(request.getOrgDescription());
        organization.setLogoUrl(request.getLogoUrl());

        return toResponseDto(organizationRepository.save(organization));
    }

    @Transactional
    public OrganizationResponseDto setVerified(Long id, boolean verified) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with id: " + id));

        organization.setVerified(verified);
        return toResponseDto(organizationRepository.save(organization));
    }

    @Transactional
    public void deleteOrganizationById(Long id) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with id: " + id));

        User user = organization.getUser();

        organizationRepository.delete(organization);
        userRepository.delete(user);
    }

    private SalesSummaryDto buildSalesSummary(Long orgId, LocalDateTime from, LocalDateTime to) {
        BigDecimal revenue = orderItemRepository.sumRevenueForOrganizationBetween(orgId, from, to);
        Long units = orderItemRepository.sumUnitsForOrganizationBetween(orgId, from, to);
        Long orders = orderItemRepository.countOrdersForOrganizationBetween(orgId, from, to);

        return new SalesSummaryDto(revenue, units, orders);
    }

    private TopProductDto getTopProduct(Long orgId) {
        List<CartActivityRepository.ProductPopularityRow> rows =
                cartActivityRepository.findTopProductsForOrganization(orgId, PageRequest.of(0, 1));

        if (rows.isEmpty()) {
            return null;
        }

        CartActivityRepository.ProductPopularityRow top = rows.get(0);
        return new TopProductDto(top.getProductId(), top.getProductName(), top.getTotalAdds());
    }

    public OrganizationSummaryDto getSummary() {
        Organization organization = getCurrentOrganization();
        Long orgId = organization.getId();

        TopProductDto topProduct = getTopProduct(orgId);

        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime startOfTomorrow = startOfToday.plusDays(1);
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime startOfNextMonth = startOfMonth.plusMonths(1);

        SalesSummaryDto salesToday = buildSalesSummary(orgId, startOfToday, startOfTomorrow);
        SalesSummaryDto salesThisMonth = buildSalesSummary(orgId, startOfMonth, startOfNextMonth);

        return new OrganizationSummaryDto(topProduct, salesToday, salesThisMonth);
    }
}