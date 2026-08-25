package com.example.kiki.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class TopProductDto {
    private Long productId;
    private String productName;
    private long cartAdds;
}
