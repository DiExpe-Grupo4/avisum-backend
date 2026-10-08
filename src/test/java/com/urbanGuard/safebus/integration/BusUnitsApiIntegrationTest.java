package com.urbanGuard.safebus.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Core Integration: BusUnitsController")
class BusUnitsApiIntegrationTest extends AbstractApiIntegrationTest {

    @Test
    @DisplayName("POST /bus-units con datos válidos devuelve 201 y la unidad queda ACTIVE")
    void createBusUnit_valid_returns201() {
        String plate = uniquePlate();

        var response = post("/api/v1/bus-units", """
                {"plateNumber":"%s","route":"R-99","latitude":-12.05,"longitude":-77.04}
                """.formatted(plate));

        assertStatus(201, response);
        assertBodyContains(response, "\"plateNumber\":\"" + plate + "\"");
        assertBodyContains(response, "\"status\":\"ACTIVE\"");
    }

    @Test
    @DisplayName("POST /bus-units sin placa devuelve 400")
    void createBusUnit_missingPlate_returns400() {
        assertStatus(400, post("/api/v1/bus-units", "{\"route\":\"R-99\"}"));
    }

    @Test
    @DisplayName("POST /bus-units con placa repetida devuelve 409")
    void createBusUnit_duplicatePlate_returns409() {
        String plate = uniquePlate();
        String body = "{\"plateNumber\":\"%s\",\"route\":\"R-99\"}".formatted(plate);
        assertStatus(201, post("/api/v1/bus-units", body));

        var duplicate = post("/api/v1/bus-units", body);

        assertStatus(409, duplicate);
        assertBodyContains(duplicate, "Placa ya registrada");
    }

    @Test
    @DisplayName("POST /bus-units con conductor asignado devuelve su nombre en la respuesta")
    void createBusUnit_withAssignedEmployee_returnsEmployeeName() {
        long employeeId = createEmployeeId();

        var response = post("/api/v1/bus-units", """
                {"plateNumber":"%s","route":"R-99","assignedEmployeeId":%d}
                """.formatted(uniquePlate(), employeeId));

        assertStatus(201, response);
        assertBodyContains(response, "\"assignedEmployeeId\":" + employeeId);
        assertBodyContains(response, "\"assignedEmployeeName\":\"Empleado Test\"");
    }

    @Test
    @DisplayName("GET /bus-units devuelve 200 e incluye las unidades semilla")
    void getAllBusUnits_returns200() {
        var response = get("/api/v1/bus-units");

        assertStatus(200, response);
        assertBodyContains(response, "ABC-1234");
    }

    @Test
    @DisplayName("GET /bus-units/{id} existente devuelve 200")
    void getBusUnitById_existing_returns200() {
        long id = createBusUnitId();

        var response = get("/api/v1/bus-units/" + id);

        assertStatus(200, response);
        assertBodyContains(response, "\"id\":" + id);
    }

    @Test
    @DisplayName("GET /bus-units/{id} inexistente devuelve 404")
    void getBusUnitById_missing_returns404() {
        assertStatus(404, get("/api/v1/bus-units/999999"));
    }

    @Test
    @DisplayName("PATCH /bus-units/{id}/location actualiza ubicación y velocidad (200)")
    void updateLocation_existing_returns200() {
        long id = createBusUnitId();

        var response = patch("/api/v1/bus-units/" + id + "/location",
                "{\"latitude\":-12.1,\"longitude\":-77.0,\"speed\":42.5}");

        assertStatus(200, response);
        assertBodyContains(response, "\"currentLatitude\":-12.1");
        assertBodyContains(response, "\"currentLongitude\":-77.0");
        assertBodyContains(response, "\"currentSpeed\":42.5");
    }

    @Test
    @DisplayName("PATCH /bus-units/{id}/location de una unidad inexistente devuelve 404")
    void updateLocation_missing_returns404() {
        assertStatus(404, patch("/api/v1/bus-units/999999/location",
                "{\"latitude\":-12.1,\"longitude\":-77.0}"));
    }
}
