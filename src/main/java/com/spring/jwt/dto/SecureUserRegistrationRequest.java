package com.spring.jwt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Secure user registration request with validation")
public class SecureUserRegistrationRequest {

    @Schema(description = "User's email address", example = "user@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", 
             message = "Email format is invalid")
    private String email;

    @Schema(description = "User's mobile number", example = "1234567890")
    @NotNull(message = "Mobile number is required")
    @Min(value = 1000000000L, message = "Mobile number must be at least 10 digits")
    @Max(value = 9999999999L, message = "Mobile number must not exceed 10 digits")
    private Long mobileNumber;

    @Schema(description = "User's password", example = "SecurePass@123")
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
             message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
    private String password;

    @Schema(description = "Password confirmation", example = "SecurePass@123")
    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;

    @Schema(description = "User's first name", example = "John")
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "First name can only contain letters and spaces")
    private String firstName;

    @Schema(description = "User's last name", example = "Doe")
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Last name can only contain letters and spaces")
    private String lastName;

    @Schema(description = "Terms and conditions acceptance", example = "true")
    @NotNull(message = "Terms and conditions must be accepted")
    @AssertTrue(message = "You must accept the terms and conditions")
    private Boolean acceptTerms;

    @Schema(description = "Privacy policy acceptance", example = "true")
    @NotNull(message = "Privacy policy must be accepted")
    @AssertTrue(message = "You must accept the privacy policy")
    private Boolean acceptPrivacyPolicy;
}