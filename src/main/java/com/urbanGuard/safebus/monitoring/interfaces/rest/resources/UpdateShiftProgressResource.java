package com.urbanGuard.safebus.monitoring.interfaces.rest.resources;

public record UpdateShiftProgressResource(
        Double distanceKm, Long durationSeconds, Integer passengerCount, Double fareCollected
) {}