package com.urbanGuard.safebus.iam.domain.model.aggregates;

import com.urbanGuard.safebus.iam.domain.model.commands.CreateEmployeeCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Core Entity: Employee")
class EmployeeTest {

    private Employee employee;

    @BeforeEach
    void setUp() {
        // Arrange
        employee = new Employee(new CreateEmployeeCommand(
                "EMP-001", "Juan Perez", "juan@safebus.com", "hash-bcrypt", "CONDUCTOR", "12345678"));
    }

    @Test
    @DisplayName("Se crea con los datos del comando y estado inicial activo")
    void shouldCreateEmployeeFromCommand() {
        assertEquals("EMP-001", employee.getEmployeeCode());
        assertEquals("Juan Perez", employee.getFullName());
        assertEquals("juan@safebus.com", employee.getEmail());
        assertEquals("12345678", employee.getDni());
        assertEquals("hash-bcrypt", employee.getPassword());
        assertEquals("CONDUCTOR", employee.getRole());
        assertTrue(employee.isActive());
        assertFalse(employee.isDeleted());
        assertEquals(0, employee.getFailedLoginAttempts());
        assertNull(employee.getLockedUntil());
    }

    @Test
    @DisplayName("Un empleado nuevo no está bloqueado y puede iniciar sesión")
    void newEmployeeIsNotLockedAndCanLogIn() {
        assertFalse(employee.isLocked());
        assertTrue(employee.canLogIn());
    }

    @Test
    @DisplayName("registerFailedAttempt incrementa el contador sin bloquear antes del límite")
    void shouldIncrementFailedAttemptsWithoutLockingBeforeLimit() {
        for (int i = 0; i < 4; i++) employee.registerFailedAttempt();

        assertEquals(4, employee.getFailedLoginAttempts());
        assertFalse(employee.isLocked());
        assertNull(employee.getLockedUntil());
    }

    @Test
    @DisplayName("Al llegar a 5 intentos fallidos la cuenta se bloquea 15 minutos")
    void shouldLockAccountAfterFiveFailedAttempts() {
        Instant before = Instant.now();

        for (int i = 0; i < 5; i++) employee.registerFailedAttempt();

        assertEquals(5, employee.getFailedLoginAttempts());
        assertTrue(employee.isLocked());
        assertNotNull(employee.getLockedUntil());
        assertTrue(employee.getLockedUntil().isAfter(before.plus(14, ChronoUnit.MINUTES)));
        assertTrue(employee.getLockedUntil().isBefore(Instant.now().plus(16, ChronoUnit.MINUTES)));
    }

    @Test
    @DisplayName("El bloqueo expira cuando lockedUntil ya pasó")
    void lockShouldExpireWhenLockedUntilIsInThePast() {
        ReflectionTestUtils.setField(employee, "lockedUntil", Instant.now().minus(1, ChronoUnit.MINUTES));

        assertFalse(employee.isLocked());
    }

    @Test
    @DisplayName("resetFailedAttempts limpia contador y bloqueo")
    void shouldResetFailedAttemptsAndUnlock() {
        for (int i = 0; i < 5; i++) employee.registerFailedAttempt();

        employee.resetFailedAttempts();

        assertEquals(0, employee.getFailedLoginAttempts());
        assertNull(employee.getLockedUntil());
        assertFalse(employee.isLocked());
    }

    @Test
    @DisplayName("deactivate impide el login y reactivate lo vuelve a permitir")
    void deactivateAndReactivateShouldToggleLoginPermission() {
        employee.deactivate();
        assertFalse(employee.isActive());
        assertFalse(employee.canLogIn());

        employee.reactivate();
        assertTrue(employee.isActive());
        assertTrue(employee.canLogIn());
    }

    @Test
    @DisplayName("softDelete marca como eliminado y desactiva, sin poder iniciar sesión")
    void softDeleteShouldMarkDeletedAndDeactivate() {
        employee.softDelete();

        assertTrue(employee.isDeleted());
        assertFalse(employee.isActive());
        assertFalse(employee.canLogIn());
    }

    @Test
    @DisplayName("Un empleado eliminado no puede iniciar sesión aunque se reactive")
    void deletedEmployeeCannotLogInEvenIfReactivated() {
        employee.softDelete();
        employee.reactivate();

        assertTrue(employee.isDeleted());
        assertFalse(employee.canLogIn());
    }

    @Test
    @DisplayName("updateProfile actualiza nombre, email, dni y rol")
    void shouldUpdateProfileData() {
        employee.updateProfile("Juan Actualizado", "nuevo@safebus.com", "87654321", "ADMIN");

        assertEquals("Juan Actualizado", employee.getFullName());
        assertEquals("nuevo@safebus.com", employee.getEmail());
        assertEquals("87654321", employee.getDni());
        assertEquals("ADMIN", employee.getRole());
        assertEquals("EMP-001", employee.getEmployeeCode(), "El código de empleado no debe cambiar");
    }
}
