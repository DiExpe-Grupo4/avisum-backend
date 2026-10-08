package com.urbanGuard.safebus.profiles.domain.model.aggregates;

import com.urbanGuard.safebus.profiles.domain.model.commands.CreateDriverProfileCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Core Entity: DriverProfile")
class DriverProfileTest {

    private DriverProfile profile;

    @BeforeEach
    void setUp() {
        profile = new DriverProfile(new CreateDriverProfileCommand(
                1L, "http://foto.com/1.jpg", "Conductor con 10 años de experiencia", "Maria Perez", "999111222"));
    }

    @Test
    @DisplayName("Se crea con los datos del comando")
    void shouldCreateFromCommand() {
        assertEquals(1L, profile.getEmployeeId());
        assertEquals("http://foto.com/1.jpg", profile.getPhotoUrl());
        assertEquals("Conductor con 10 años de experiencia", profile.getBio());
        assertEquals("Maria Perez", profile.getEmergencyContactName());
        assertEquals("999111222", profile.getEmergencyContactPhone());
    }

    @Test
    @DisplayName("updateProfile reemplaza foto, bio y contacto de emergencia")
    void updateProfileShouldReplaceAllEditableFields() {
        profile.updateProfile("http://foto.com/2.jpg", "Nueva bio", "Carlos Ruiz", "988777666");

        assertEquals("http://foto.com/2.jpg", profile.getPhotoUrl());
        assertEquals("Nueva bio", profile.getBio());
        assertEquals("Carlos Ruiz", profile.getEmergencyContactName());
        assertEquals("988777666", profile.getEmergencyContactPhone());
        assertEquals(1L, profile.getEmployeeId(), "El employeeId no debe cambiar");
    }
}
