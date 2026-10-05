package com.urbanGuard.safebus.monitoring.interfaces.rest.resources;
import jakarta.validation.constraints.NotBlank;
public record CreateBusUnitResource(@NotBlank String plateNumber, @NotBlank String route, Double latitude, Double longitude, Long assignedEmployeeId) {}