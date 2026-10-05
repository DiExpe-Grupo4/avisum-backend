package com.urbanGuard.safebus.monitoring.domain.model.commands;

public record UpdateShiftProgressCommand(
        Long shiftId, Double distanceKm, Long durationSeconds,
        Integer passengerCount, Double fareCollected
) {}