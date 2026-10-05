package com.urbanGuard.safebus.monitoring.domain.model.aggregates;

import com.urbanGuard.safebus.monitoring.domain.model.commands.StartShiftCommand;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long employeeId;

    @Column(nullable = false)
    private Long busUnitId;

    @Column(nullable = false)
    private String routeName;

    private String routeOrigin;
    private String routeDestination;

    @Column(nullable = false)
    private Double distanceKm = 0.0;

    @Column(nullable = false)
    private Long durationSeconds = 0L;

    @Column(nullable = false)
    private Integer passengerCount = 0;

    @Column(nullable = false)
    private Double fareCollected = 0.0;

    @Column(nullable = false)
    private String status; // ACTIVE, FINISHED

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant startedAt;

    private Instant endedAt;

    protected Shift() {}

    public Shift(StartShiftCommand command, String routeName) {
        this.employeeId = command.employeeId();
        this.busUnitId = command.busUnitId();
        this.routeName = routeName;
        this.routeOrigin = command.routeOrigin();
        this.routeDestination = command.routeDestination();
        this.status = "ACTIVE";
    }

    public void finish(Double distanceKm, Long durationSeconds, Integer passengerCount, Double fareCollected) {
        this.distanceKm = distanceKm != null ? distanceKm : this.distanceKm;
        this.durationSeconds = durationSeconds != null ? durationSeconds : this.durationSeconds;
        this.passengerCount = passengerCount != null ? passengerCount : this.passengerCount;
        this.fareCollected = fareCollected != null ? fareCollected : this.fareCollected;
        this.status = "FINISHED";
        this.endedAt = Instant.now();
    }
    /** Actualiza las métricas de un turno que sigue ACTIVO, sin finalizarlo (para la vista en vivo). */
    public void updateProgress(Double distanceKm, Long durationSeconds, Integer passengerCount, Double fareCollected) {
        if (!isActive()) return;
        this.distanceKm = distanceKm != null ? distanceKm : this.distanceKm;
        this.durationSeconds = durationSeconds != null ? durationSeconds : this.durationSeconds;
        this.passengerCount = passengerCount != null ? passengerCount : this.passengerCount;
        this.fareCollected = fareCollected != null ? fareCollected : this.fareCollected;
    }
    public boolean isActive() {
        return "ACTIVE".equals(this.status);
    }
}