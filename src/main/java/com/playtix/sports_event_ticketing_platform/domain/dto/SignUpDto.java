package com.playtix.sports_event_ticketing_platform.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpDto(
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "First name can only contain letters, spaces, and hyphens")
    String firstname,
    
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "Last name can only contain letters, spaces, and hyphens")
    String lastname,
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must be less than 100 characters")
    String email,
    
    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^09[0-9]{9}$",
        message = "Phone number must be 10-15 digits and can start with +"
    )
    String phoneNumber,
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
        message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character"
    )
    String password,
    
    @NotBlank(message = "City is required")
    @Size(min = 2, max = 50, message = "City must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "City can only contain letters, spaces, and hyphens")
    String city
) {}
