package com.example.kiki.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AdminSummaryDto {
    private long totalCheckouts;
    private SalesSummaryDto salesToday;
    private SalesSummaryDto salesThisMonth;
    private long totalUsers;
    private long totalOrganizations;
    private long verifiedOrganizations;
    private long totalProducts;
}
