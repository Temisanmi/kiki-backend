package com.example.kiki.dto.organization;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrganizationRequest {
    @NotBlank(message = "Organization name is required")
    @Size(min = 2, max = 100, message = "Invalid organization name length")
    private String orgName;

    @Size(max = 1000, message = "Description must be under 1000 characters")
    private String orgDescription;

    private String logoUrl;
}
