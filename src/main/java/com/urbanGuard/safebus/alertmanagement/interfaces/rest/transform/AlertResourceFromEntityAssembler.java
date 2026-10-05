package com.urbanGuard.safebus.alertmanagement.interfaces.rest.transform;
import com.urbanGuard.safebus.alertmanagement.domain.model.aggregates.Alert;
import com.urbanGuard.safebus.alertmanagement.interfaces.rest.resources.AlertResource;
public class AlertResourceFromEntityAssembler {
    public static AlertResource toResourceFromEntity(Alert e) {
        return new AlertResource(e.getId(), e.getEmployeeId(), e.getBusUnitId(), e.getAlertType(), e.getStatus(), e.getDescription(), e.getLatitude(), e.getLongitude(), e.getCreatedAt(), e.getUpdatedAt());
    }
}