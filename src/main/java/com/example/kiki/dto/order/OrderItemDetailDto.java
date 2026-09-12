package com.example.kiki.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
public class OrderItemDetailDto {
    private Long productId;
    private String productName;
    private String organizationName;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subTotal;
}
