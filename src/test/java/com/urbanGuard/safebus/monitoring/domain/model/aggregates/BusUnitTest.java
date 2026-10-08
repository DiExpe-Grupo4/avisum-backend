package com.urbanGuard.safebus.monitoring.domain.model.aggregates;

import com.urbanGuard.safebus.monitoring.domain.model.commands.CreateBusUnitCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Core Entity: BusUnit")
class BusUnitTest {

    private BusUnit bus;

    @BeforeEach
    void setUp() {
        bus = new BusUnit(new CreateBusUnitCommand("ABC-123", "Ruta 42", -12.05, -77.04, 1L));
    }

    @Test
    @DisplayName("Se crea ACTIVA con placa, ruta, ubicación inicial y conductor asignado")
    void shouldCreateActiveBusUnitFromCommand() {
        assertEquals("ABC-123", bus.getPlateNumber());
        assertEquals("Ruta 42", bus.getRoute());
        assertEquals("ACTIVE", bus.getStatus());
        assertEquals(-12.05, bus.getCurrentLatitude());
        assertEquals(-77.04, bus.getCurrentLongitude());
        assertEquals(1L, bus.getAssignedEmployeeId());
        assertNull(bus.getCurrentSpeed());
    }

    @Test
    @DisplayName("updateLocation actualiza latitud, longitud y velocidad")
    void updateLocationShouldChangeCoordinatesAndSpeed() {
        bus.updateLocation(-12.10, -77.00, 45.5);

        assertEquals(-12.10, bus.getCurrentLatitude());
        assertEquals(-77.00, bus.getCurrentLongitude());
        assertEquals(45.5, bus.getCurrentSpeed());
    }

    @Test
    @DisplayName("updateLocation con velocidad nula conserva la velocidad anterior")
    void updateLocationWithNullSpeedShouldKeepPreviousSpeed() {
        bus.updateLocation(-12.10, -77.00, 40.0);

        bus.updateLocation(-12.20, -77.10, null);

        assertEquals(-12.20, bus.getCurrentLatitude());
        assertEquals(-77.10, bus.getCurrentLongitude());
        assertEquals(40.0, bus.getCurrentSpeed());
    }

    @Test
    @DisplayName("assignTo cambia el conductor asignado")
    void assignToShouldChangeAssignedEmployee() {
        bus.assignTo(7L);

        assertEquals(7L, bus.getAssignedEmployeeId());
    }
}
