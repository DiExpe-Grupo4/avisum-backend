package com.urbanGuard.safebus.alertmanagement.domain.model.aggregates;

import com.urbanGuard.safebus.alertmanagement.domain.model.commands.CreateAlertCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Core Entity: Alert")
class AlertTest {

    private Alert alert;

    @BeforeEach
    void setUp() {
        alert = new Alert(new CreateAlertCommand(1L, 10L, "PANIC", "Botón de pánico activado", -12.05, -77.04));
    }

    @Test
    @DisplayName("Se crea con estado ACTIVE y los datos del comando")
    void shouldCreateActiveAlertFromCommand() {
        assertEquals(1L, alert.getEmployeeId());
        assertEquals(10L, alert.getBusUnitId());
        assertEquals("PANIC", alert.getAlertType());
        assertEquals("ACTIVE", alert.getStatus());
        assertEquals("Botón de pánico activado", alert.getDescription());
        assertEquals(-12.05, alert.getLatitude());
        assertEquals(-77.04, alert.getLongitude());
    }

    @Test
    @DisplayName("resolve cambia el estado a RESOLVED")
    void resolveShouldChangeStatusToResolved() {
        alert.resolve();

        assertEquals("RESOLVED", alert.getStatus());
    }

    @Test
    @DisplayName("dismiss cambia el estado a DISMISSED")
    void dismissShouldChangeStatusToDismissed() {
        alert.dismiss();

        assertEquals("DISMISSED", alert.getStatus());
    }
}
