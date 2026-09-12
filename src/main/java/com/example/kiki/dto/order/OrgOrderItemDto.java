package com.example.kiki.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class OrgOrderItemDto {
    private Long orderId;
    private LocalDateTime orderedAt;
    private String buyerUsername;
    private Long productId;
    private String productName;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subTotal;
}
