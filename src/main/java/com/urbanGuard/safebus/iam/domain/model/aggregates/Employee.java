package com.urbanGuard.safebus.iam.domain.model.aggregates;

import com.urbanGuard.safebus.iam.domain.model.commands.CreateEmployeeCommand;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Employee {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MINUTES = 15;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String employeeCode;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String dni;

    @Column(nullable = false)
    private String password; // hash BCrypt, nunca texto plano

    @Column(nullable = false)
    private String role; // CONDUCTOR, ADMIN

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(nullable = false)
    private int failedLoginAttempts = 0;

    private Instant lockedUntil;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(nullable = false)
    @LastModifiedDate
    private Instant updatedAt;

    protected Employee() {}

    public Employee(CreateEmployeeCommand command) {
        this.employeeCode = command.employeeCode();
        this.fullName = command.fullName();
        this.email = command.email();
        this.dni = command.dni();
        this.password = command.password();
        this.role = command.role();
    }

    public boolean isLocked() {
        return lockedUntil != null && Instant.now().isBefore(lockedUntil);
    }

    public void registerFailedAttempt() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= MAX_FAILED_ATTEMPTS) {
            this.lockedUntil = Instant.now().plus(LOCK_DURATION_MINUTES, ChronoUnit.MINUTES);
        }
    }

    public void resetFailedAttempts() {
        this.failedLoginAttempts = 0;
        this.lockedUntil = null;
    }

    /** false si está desactivado por el admin o eliminado (soft delete). */
    public boolean canLogIn() {
        return active && !deleted;
    }

    public void updateProfile(String fullName, String email, String dni, String role) {
        this.fullName = fullName;
        this.email = email;
        this.dni = dni;
        this.role = role;
    }

    public void deactivate() { this.active = false; }

    public void reactivate() { this.active = true; }

    /** Soft delete: no se borra la fila para no romper el historial de turnos. */
    public void softDelete() {
        this.deleted = true;
        this.active = false;
    }
}