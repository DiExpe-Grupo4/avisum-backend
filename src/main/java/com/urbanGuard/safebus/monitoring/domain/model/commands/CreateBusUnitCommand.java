package com.urbanGuard.safebus.monitoring.domain.model.commands;
public record CreateBusUnitCommand(String plateNumber, String route, Double latitude, Double longitude, Long assignedEmployeeId) {}