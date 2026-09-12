package com.example.kiki.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class OrderDetailDto {
    private Long orderId;
    private String username;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private List<OrderItemDetailDto> items;
}
