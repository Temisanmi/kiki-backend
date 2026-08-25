package com.example.kiki.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
public class SalesSummaryDto {
    private BigDecimal totalRevenue;
    private long unitsSold;
    private long orderCount;
}
