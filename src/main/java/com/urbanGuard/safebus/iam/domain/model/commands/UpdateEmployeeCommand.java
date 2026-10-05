package com.urbanGuard.safebus.iam.domain.model.commands;

public record UpdateEmployeeCommand(Long employeeId, String fullName, String email, String dni, String role) {}