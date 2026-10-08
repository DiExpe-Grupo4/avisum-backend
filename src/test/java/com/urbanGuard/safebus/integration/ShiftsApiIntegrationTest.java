package com.urbanGuard.safebus.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Core Integration: ShiftsController")
class ShiftsApiIntegrationTest extends AbstractApiIntegrationTest {

    private String startBody(long employeeId, long busUnitId) {
        return """
                {"employeeId":%d,"busUnitId":%d,"routeOrigin":"Villa El Salvador","routeDestination":"Miraflores"}
                """.formatted(employeeId, busUnitId);
    }

    @Test
    @DisplayName("POST /shifts inicia un turno ACTIVE (201) con la ruta de la unidad")
    void startShift_valid_returns201() {
        long employeeId = createEmployeeId();
        long busId = createBusUnitId();

        var response = post("/api/v1/shifts", startBody(employeeId, busId));

        assertStatus(201, response);
        assertBodyContains(response, "\"status\":\"ACTIVE\"");
        assertBodyContains(response, "\"routeName\":\"R-TEST\"");
        assertBodyContains(response, "\"employeeId\":" + employeeId);
    }

    @Test
    @DisplayName("POST /shifts sin busUnitId devuelve 400")
    void startShift_missingBusUnit_returns400() {
        assertStatus(400, post("/api/v1/shifts", "{\"employeeId\":1}"));
    }

    @Test
    @DisplayName("POST /shifts con empleado inexistente devuelve 409")
    void startShift_unknownEmployee_returns409() {
        var response = post("/api/v1/shifts", startBody(999999L, createBusUnitId()));

        assertStatus(409, response);
        assertBodyContains(response, "Empleado no encontrado");
    }

    @Test
    @DisplayName("POST /shifts con unidad inexistente devuelve 409")
    void startShift_unknownBus_returns409() {
        var response = post("/api/v1/shifts", startBody(createEmployeeId(), 999999L));

        assertStatus(409, response);
        assertBodyContains(response, "Unidad de bus no encontrada");
    }

    @Test
    @DisplayName("POST /shifts en una unidad con turno activo devuelve 409")
    void startShift_busAlreadyActive_returns409() {
        long busId = createBusUnitId();
        assertStatus(201, post("/api/v1/shifts", startBody(createEmployeeId(), busId)));

        var response = post("/api/v1/shifts", startBody(createEmployeeId(), busId));

        assertStatus(409, response);
        assertBodyContains(response, "La unidad ya tiene un turno activo");
    }

    @Test
    @DisplayName("POST /shifts de un empleado con turno activo devuelve 409")
    void startShift_employeeAlreadyActive_returns409() {
        long employeeId = createEmployeeId();
        assertStatus(201, post("/api/v1/shifts", startBody(employeeId, createBusUnitId())));

        var response = post("/api/v1/shifts", startBody(employeeId, createBusUnitId()));

        assertStatus(409, response);
        assertBodyContains(response, "El empleado ya tiene un turno activo");
    }

    @Test
    @DisplayName("PATCH /shifts/{id}/progress actualiza métricas y mantiene ACTIVE (200)")
    void updateProgress_activeShift_returns200() {
        long shiftId = idOf(post("/api/v1/shifts", startBody(createEmployeeId(), createBusUnitId())));

        var response = patch("/api/v1/shifts/" + shiftId + "/progress",
                "{\"distanceKm\":3.5,\"durationSeconds\":300,\"passengerCount\":12,\"fareCollected\":36.0}");

        assertStatus(200, response);
        assertBodyContains(response, "\"distanceKm\":3.5");
        assertBodyContains(response, "\"passengerCount\":12");
        assertBodyContains(response, "\"status\":\"ACTIVE\"");
    }

    @Test
    @DisplayName("PATCH /shifts/{id}/end finaliza el turno (200) y no se puede finalizar dos veces (409)")
    void endShift_thenEndAgain() {
        long shiftId = idOf(post("/api/v1/shifts", startBody(createEmployeeId(), createBusUnitId())));
        String endBody = "{\"distanceKm\":10.0,\"durationSeconds\":3600,\"passengerCount\":40,\"fareCollected\":120.0}";

        var ended = patch("/api/v1/shifts/" + shiftId + "/end", endBody);
        assertStatus(200, ended);
        assertBodyContains(ended, "\"status\":\"FINISHED\"");
        assertBodyContains(ended, "\"fareCollected\":120.0");

        var again = patch("/api/v1/shifts/" + shiftId + "/end", endBody);
        assertStatus(409, again);
        assertBodyContains(again, "El turno ya fue finalizado");
    }

    @Test
    @DisplayName("PATCH /shifts/{id}/progress de un turno finalizado devuelve 409")
    void updateProgress_finishedShift_returns409() {
        long shiftId = idOf(post("/api/v1/shifts", startBody(createEmployeeId(), createBusUnitId())));
        assertStatus(200, patch("/api/v1/shifts/" + shiftId + "/end", "{}"));

        assertStatus(409, patch("/api/v1/shifts/" + shiftId + "/progress", "{\"distanceKm\":1.0}"));
    }

    @Test
    @DisplayName("PATCH /shifts/{id}/end de un turno inexistente devuelve 409")
    void endShift_missing_returns409() {
        var response = patch("/api/v1/shifts/999999/end", "{}");

        assertStatus(409, response);
        assertBodyContains(response, "Turno no encontrado");
    }

    @Test
    @DisplayName("GET /shifts/{id} existente devuelve 200 e inexistente devuelve 404")
    void getShiftById() {
        long shiftId = idOf(post("/api/v1/shifts", startBody(createEmployeeId(), createBusUnitId())));

        assertStatus(200, get("/api/v1/shifts/" + shiftId));
        assertStatus(404, get("/api/v1/shifts/999999"));
    }

    @Test
    @DisplayName("GET /shifts devuelve 200")
    void getAllShifts_returns200() {
        assertStatus(200, get("/api/v1/shifts"));
    }

    @Test
    @DisplayName("GET /shifts/employee/{id} devuelve el historial del empleado (200)")
    void getShiftsByEmployee_returns200() {
        long employeeId = createEmployeeId();
        assertStatus(201, post("/api/v1/shifts", startBody(employeeId, createBusUnitId())));

        var response = get("/api/v1/shifts/employee/" + employeeId);

        assertStatus(200, response);
        assertBodyContains(response, "\"employeeId\":" + employeeId);
    }

    @Test
    @DisplayName("GET /shifts/bus-unit/{id}/active devuelve 200 con turno activo y 404 al finalizarlo")
    void getActiveShiftByBusUnit() {
        long busId = createBusUnitId();
        long shiftId = idOf(post("/api/v1/shifts", startBody(createEmployeeId(), busId)));

        var active = get("/api/v1/shifts/bus-unit/" + busId + "/active");
        assertStatus(200, active);
        assertBodyContains(active, "\"id\":" + shiftId);

        assertStatus(200, patch("/api/v1/shifts/" + shiftId + "/end", "{}"));
        assertStatus(404, get("/api/v1/shifts/bus-unit/" + busId + "/active"));
    }
}
