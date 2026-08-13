package com.example.kiki.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "Username is required!")
    @Size(min = 3, max = 20, message = "Invalid username length")
    private String username;

    @NotBlank(message = "Password is required!")
    @Size(min = 6, message = "Invalid password length")
    private String password;
}
