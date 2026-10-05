package com.urbanGuard.safebus.monitoring.domain.model.aggregates;

import com.urbanGuard.safebus.monitoring.domain.model.commands.CreateBusUnitCommand;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.Instant;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class BusUnit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String plateNumber;
    @Column(nullable = false)
    private String route;
    @Column(nullable = false)
    private String status; // ACTIVE, INACTIVE, MAINTENANCE
    private Double currentLatitude;
    private Double currentLongitude;
    private Double currentSpeed;
    /** Conductor asignado de forma permanente a esta unidad (no depende de si tiene un turno activo). */
    private Long assignedEmployeeId;
    @Column(nullable = false, updatable = false) @CreatedDate
    private Instant createdAt;
    @Column(nullable = false) @LastModifiedDate
    private Instant updatedAt;

    protected BusUnit() {}
    public BusUnit(CreateBusUnitCommand command) {
        this.plateNumber = command.plateNumber();
        this.route = command.route();
        this.status = "ACTIVE";
        this.currentLatitude = command.latitude();
        this.currentLongitude = command.longitude();
        this.assignedEmployeeId = command.assignedEmployeeId();
    }
    public void updateLocation(Double lat, Double lon, Double speed) {
        this.currentLatitude = lat;
        this.currentLongitude = lon;
        if (speed != null) this.currentSpeed = speed;
    }
    public void assignTo(Long employeeId) {
        this.assignedEmployeeId = employeeId;
    }
}