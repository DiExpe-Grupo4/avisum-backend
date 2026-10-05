package com.urbanGuard.safebus.alertmanagement.interfaces.rest.resources;
import java.time.Instant;
public record AlertResource(Long id, Long employeeId, Long busUnitId, String alertType, String status, String description, Double latitude, Double longitude, Instant createdAt, Instant updatedAt) {}