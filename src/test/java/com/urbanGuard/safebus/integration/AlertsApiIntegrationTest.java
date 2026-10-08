package com.urbanGuard.safebus.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Core Integration: AlertsController")
class AlertsApiIntegrationTest extends AbstractApiIntegrationTest {

    private String alertBody(long employeeId, long busUnitId) {
        return """
                {"employeeId":%d,"busUnitId":%d,"alertType":"PANIC","description":"Boton de panico","latitude":-12.0464,"longitude":-77.0428}
                """.formatted(employeeId, busUnitId);
    }

    @Test
    @DisplayName("POST /alerts con datos válidos devuelve 201 y la alerta queda ACTIVE")
    void createAlert_valid_returns201() {
        var response = post("/api/v1/alerts", alertBody(createEmployeeId(), createBusUnitId()));

        assertStatus(201, response);
        assertBodyContains(response, "\"alertType\":\"PANIC\"");
        assertBodyContains(response, "\"status\":\"ACTIVE\"");
    }

    @Test
    @DisplayName("POST /alerts sin tipo de alerta devuelve 400")
    void createAlert_missingType_returns400() {
        assertStatus(400, post("/api/v1/alerts", "{\"employeeId\":1,\"busUnitId\":1}"));
    }

    @Test
    @DisplayName("POST /alerts sin employeeId devuelve 400")
    void createAlert_missingEmployee_returns400() {
        assertStatus(400, post("/api/v1/alerts", "{\"busUnitId\":1,\"alertType\":\"PANIC\"}"));
    }

    @Test
    @DisplayName("GET /alerts devuelve 200")
    void getAllAlerts_returns200() {
        assertStatus(200, get("/api/v1/alerts"));
    }

    @Test
    @DisplayName("GET /alerts/{id} existente devuelve 200 e inexistente devuelve 404")
    void getAlertById() {
        long id = idOf(post("/api/v1/alerts", alertBody(createEmployeeId(), createBusUnitId())));

        assertStatus(200, get("/api/v1/alerts/" + id));
        assertStatus(404, get("/api/v1/alerts/999999"));
    }

    @Test
    @DisplayName("GET /alerts/employee/{id} devuelve las alertas del empleado (200)")
    void getAlertsByEmployee_returns200() {
        long employeeId = createEmployeeId();
        assertStatus(201, post("/api/v1/alerts", alertBody(employeeId, createBusUnitId())));

        var response = get("/api/v1/alerts/employee/" + employeeId);

        assertStatus(200, response);
        assertBodyContains(response, "\"employeeId\":" + employeeId);
    }

    @Test
    @DisplayName("PATCH /alerts/{id}/resolve cambia el estado a RESOLVED (200)")
    void resolveAlert_existing_returns200() {
        long id = idOf(post("/api/v1/alerts", alertBody(createEmployeeId(), createBusUnitId())));

        var response = patch("/api/v1/alerts/" + id + "/resolve", "");

        assertStatus(200, response);
        assertBodyContains(response, "\"status\":\"RESOLVED\"");
    }

    @Test
    @DisplayName("PATCH /alerts/{id}/resolve inexistente devuelve 404")
    void resolveAlert_missing_returns404() {
        assertStatus(404, patch("/api/v1/alerts/999999/resolve", ""));
    }
}
