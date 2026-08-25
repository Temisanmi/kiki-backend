package com.example.kiki.service;

import com.example.kiki.dto.analytics.OrganizationSummaryDto;
import com.example.kiki.dto.analytics.SalesSummaryDto;
import com.example.kiki.dto.analytics.TopProductDto;
import com.example.kiki.entity.Organization;
import com.example.kiki.exception.ForbiddenOperationException;
import com.example.kiki.repository.CartActivityRepository;
import com.example.kiki.repository.OrderItemRepository;
import com.example.kiki.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final OrganizationRepository organizationRepository;
    private final CartActivityRepository cartActivityRepository;
    private final OrderItemRepository orderItemRepository;

    private Organization getCurrentOrganization() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return organizationRepository.findByUser_Username(username)
                .orElseThrow(() -> new ForbiddenOperationException("No organization profile found for this account"));
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

    private TopProductDto getTopProduct(Long orgId) {
        List<CartActivityRepository.ProductPopularityRow> rows =
                cartActivityRepository.findTopProductsForOrganization(orgId, PageRequest.of(0, 1));

        if (rows.isEmpty()) {
            return null;
        }

        CartActivityRepository.ProductPopularityRow top = rows.get(0);
        return new TopProductDto(top.getProductId(), top.getProductName(), top.getTotalAdds());
    }

    private SalesSummaryDto buildSalesSummary(Long orgId, LocalDateTime from, LocalDateTime to) {
        BigDecimal revenue = orderItemRepository.sumRevenueForOrganizationBetween(orgId, from, to);
        Long units = orderItemRepository.sumUnitsForOrganizationBetween(orgId, from, to);
        Long orders = orderItemRepository.countOrdersForOrganizationBetween(orgId, from, to);

        return new SalesSummaryDto(revenue, units, orders);
    }
}