package com.example.kiki.dto.organization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class OrganizationResponseDto {
    private Long id;
    private String orgName;
    private String orgDescription;
    private String logoUrl;
    private boolean verified;
}
