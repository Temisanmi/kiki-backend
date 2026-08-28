package com.example.kiki.dto.cart;

import com.example.kiki.entity.Organization;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class CartItemResponseDto {
    private Long cartItemId;
    private Long productId;
    private String productName;
    private String productDescription;
    private String productImageUrl;
    private BigDecimal unitPrice;
    private String organizationName;
    private Integer quantity;
    private BigDecimal subtotal;
}
