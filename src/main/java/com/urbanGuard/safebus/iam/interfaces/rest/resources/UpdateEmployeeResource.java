package com.urbanGuard.safebus.iam.interfaces.rest.resources;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateEmployeeResource(
        @NotBlank String fullName,
        @NotBlank @Email String email,
        @NotBlank String role,
        @NotBlank @Pattern(regexp = "\\d{8}", message = "El DNI debe tener 8 dígitos") String dni
) {}