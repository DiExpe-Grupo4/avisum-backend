package com.urbanGuard.safebus.monitoring.domain.model.aggregates;

import com.urbanGuard.safebus.monitoring.domain.model.commands.RegisterSensorCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Core Entity: Sensor")
class SensorTest {

    private Sensor sensor;

    @BeforeEach
    void setUp() {
        sensor = new Sensor(new RegisterSensorCommand("SEN-001", "GPS", 10L));
    }

    @Test
    @DisplayName("Se registra ONLINE sin lecturas previas")
    void shouldRegisterOnlineWithoutReading() {
        assertEquals("SEN-001", sensor.getSensorCode());
        assertEquals("GPS", sensor.getSensorType());
        assertEquals(10L, sensor.getBusUnitId());
        assertEquals("ONLINE", sensor.getStatus());
        assertNull(sensor.getLastReading());
    }

    @Test
    @DisplayName("updateReading guarda la última lectura")
    void updateReadingShouldStoreLastReading() {
        sensor.updateReading("-12.05,-77.04");

        assertEquals("-12.05,-77.04", sensor.getLastReading());
    }

    @Test
    @DisplayName("setOffline cambia el estado a OFFLINE")
    void setOfflineShouldChangeStatus() {
        sensor.setOffline();

        assertEquals("OFFLINE", sensor.getStatus());
    }
}
