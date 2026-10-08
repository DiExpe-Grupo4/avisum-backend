package com.urbanGuard.safebus.monitoring.domain.model.aggregates;

import com.urbanGuard.safebus.monitoring.domain.model.commands.StartShiftCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Core Entity: Shift")
class ShiftTest {

    private Shift shift;

    @BeforeEach
    void setUp() {
        shift = new Shift(new StartShiftCommand(1L, 10L, "Villa El Salvador", "Miraflores"), "Ruta 42");
    }

    @Test
    @DisplayName("Se crea ACTIVO con las métricas en cero")
    void shouldStartActiveWithZeroMetrics() {
        assertEquals(1L, shift.getEmployeeId());
        assertEquals(10L, shift.getBusUnitId());
        assertEquals("Ruta 42", shift.getRouteName());
        assertEquals("Villa El Salvador", shift.getRouteOrigin());
        assertEquals("Miraflores", shift.getRouteDestination());
        assertEquals("ACTIVE", shift.getStatus());
        assertTrue(shift.isActive());
        assertEquals(0.0, shift.getDistanceKm());
        assertEquals(0L, shift.getDurationSeconds());
        assertEquals(0, shift.getPassengerCount());
        assertEquals(0.0, shift.getFareCollected());
        assertNull(shift.getEndedAt());
    }

    @Test
    @DisplayName("finish guarda las métricas, cambia a FINISHED y registra endedAt")
    void finishShouldStoreMetricsAndCloseShift() {
        shift.finish(12.5, 3600L, 40, 100.0);

        assertEquals(12.5, shift.getDistanceKm());
        assertEquals(3600L, shift.getDurationSeconds());
        assertEquals(40, shift.getPassengerCount());
        assertEquals(100.0, shift.getFareCollected());
        assertEquals("FINISHED", shift.getStatus());
        assertFalse(shift.isActive());
        assertNotNull(shift.getEndedAt());
    }

    @Test
    @DisplayName("finish con valores nulos conserva las métricas previas")
    void finishWithNullsShouldKeepPreviousValues() {
        shift.updateProgress(5.0, 600L, 10, 30.0);

        shift.finish(null, null, null, null);

        assertEquals(5.0, shift.getDistanceKm());
        assertEquals(600L, shift.getDurationSeconds());
        assertEquals(10, shift.getPassengerCount());
        assertEquals(30.0, shift.getFareCollected());
        assertEquals("FINISHED", shift.getStatus());
    }

    @Test
    @DisplayName("updateProgress actualiza métricas y mantiene el turno ACTIVO")
    void updateProgressShouldUpdateMetricsWhileActive() {
        shift.updateProgress(3.2, 300L, 8, 24.0);

        assertEquals(3.2, shift.getDistanceKm());
        assertEquals(300L, shift.getDurationSeconds());
        assertEquals(8, shift.getPassengerCount());
        assertEquals(24.0, shift.getFareCollected());
        assertTrue(shift.isActive());
        assertNull(shift.getEndedAt());
    }

    @Test
    @DisplayName("updateProgress con nulos conserva los valores anteriores")
    void updateProgressWithNullsShouldKeepPreviousValues() {
        shift.updateProgress(3.2, 300L, 8, 24.0);

        shift.updateProgress(null, null, null, null);

        assertEquals(3.2, shift.getDistanceKm());
        assertEquals(300L, shift.getDurationSeconds());
        assertEquals(8, shift.getPassengerCount());
        assertEquals(24.0, shift.getFareCollected());
    }

    @Test
    @DisplayName("updateProgress no modifica un turno ya FINALIZADO")
    void updateProgressShouldBeIgnoredWhenShiftIsFinished() {
        shift.finish(10.0, 1000L, 20, 60.0);

        shift.updateProgress(99.0, 9999L, 99, 999.0);

        assertEquals(10.0, shift.getDistanceKm());
        assertEquals(1000L, shift.getDurationSeconds());
        assertEquals(20, shift.getPassengerCount());
        assertEquals(60.0, shift.getFareCollected());
    }
}
