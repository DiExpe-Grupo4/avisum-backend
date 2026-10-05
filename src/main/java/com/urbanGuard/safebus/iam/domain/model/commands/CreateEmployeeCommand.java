package com.urbanGuard.safebus.iam.domain.model.commands;

public record CreateEmployeeCommand(String employeeCode, String fullName, String email, String password, String role, String dni) {}