package com.example.kiki.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class OrderResponseDto {
    private Long orderId;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
}
