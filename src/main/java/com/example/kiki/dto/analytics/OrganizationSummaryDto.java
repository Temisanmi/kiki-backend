package com.example.kiki.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class OrganizationSummaryDto {
    private TopProductDto topProduct;
    private SalesSummaryDto salesToday;
    private SalesSummaryDto salesThisMonth;
}
