package com.urbanGuard.safebus.monitoring.domain.model.aggregates;

import com.urbanGuard.safebus.monitoring.domain.model.commands.RegisterPassengerCountCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Core Entity: PassengerCount")
class PassengerCountTest {

    @Test
    @DisplayName("Se crea con los totales del comando")
    void shouldCreateFromCommand() {
        var pc = new PassengerCount(new RegisterPassengerCountCommand(1L, 10L, 30, 12, 18, true));

        assertEquals(1L, pc.getShiftId());
        assertEquals(10L, pc.getBusUnitId());
        assertEquals(30, pc.getTotalBoarded());
        assertEquals(12, pc.getTotalAlighted());
        assertEquals(18, pc.getTotalAboard());
        assertTrue(pc.getAnomaly());
    }

    @Test
    @DisplayName("Si anomaly es nulo se asume false")
    void nullAnomalyShouldDefaultToFalse() {
        var pc = new PassengerCount(new RegisterPassengerCountCommand(1L, 10L, 30, 12, 18, null));

        assertFalse(pc.getAnomaly());
    }

    @Test
    @DisplayName("anomaly false se conserva como false")
    void falseAnomalyShouldBeKept() {
        var pc = new PassengerCount(new RegisterPassengerCountCommand(1L, 10L, 5, 0, 5, false));

        assertFalse(pc.getAnomaly());
    }
}
