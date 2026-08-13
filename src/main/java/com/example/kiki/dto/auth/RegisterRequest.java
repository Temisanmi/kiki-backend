package com.example.kiki.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank(message = "First name is required")
    @Size(min = 3, max = 20, message = "Invalid first name length")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 3, max = 20, message = "Invalid last name length")
    private String lastName;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Invalid username length")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "\\d{11}", message = "Phone Number must be exactly 11 digits")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
