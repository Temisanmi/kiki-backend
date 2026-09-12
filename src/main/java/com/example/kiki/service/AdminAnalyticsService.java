package com.example.kiki.service;

import com.example.kiki.dto.analytics.AdminSummaryDto;
import com.example.kiki.dto.analytics.SalesSummaryDto;
import com.example.kiki.repository.OrderItemRepository;
import com.example.kiki.repository.OrderRepository;
import com.example.kiki.repository.OrganizationRepository;
import com.example.kiki.repository.ProductRepository;
import com.example.kiki.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminAnalyticsService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final ProductRepository productRepository;

    private SalesSummaryDto buildSalesSummary(LocalDateTime from, LocalDateTime to) {
        BigDecimal revenue = orderRepository.sumRevenueBetween(from, to);
        Long units = orderItemRepository.sumUnitsBetween(from, to);
        long orders = orderRepository.countByCreatedAtBetween(from, to);

        return new SalesSummaryDto(revenue, units, orders);
    }

    public AdminSummaryDto getSummary() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime startOfTomorrow = startOfToday.plusDays(1);
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime startOfNextMonth = startOfMonth.plusMonths(1);

        SalesSummaryDto salesToday = buildSalesSummary(startOfToday, startOfTomorrow);
        SalesSummaryDto salesThisMonth = buildSalesSummary(startOfMonth, startOfNextMonth);

        long totalCheckouts = orderRepository.count();
        long totalUsers = userRepository.count();
        long totalOrganizations = organizationRepository.count();
        long verifiedOrganizations = organizationRepository.countByVerified(true);
        long totalProducts = productRepository.count();

        return new AdminSummaryDto(
                totalCheckouts,
                salesToday,
                salesThisMonth,
                totalUsers,
                totalOrganizations,
                verifiedOrganizations,
                totalProducts
        );
    }
}