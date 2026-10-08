package com.urbanGuard.safebus.bdd.support;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Crea los datos de apoyo (precondiciones "Dado") a través de la propia API. */
public final class Fixtures {

    private Fixtures() {}

    public static void registerEmployee(String password) {
        var r = Api.post("/api/v1/employees", """
                {"fullName":"Empleado Test","email":"%s","password":"%s","role":"CONDUCTOR","dni":"%s"}
                """.formatted(Api.uniqueEmail(), password, Api.uniqueDni()));
        assertEquals(201, r.statusCode(), () -> "No se pudo registrar el conductor: " + r.body());
        World.employeeId = Json.longField(r.body(), "id");
        World.employeeCode = Json.stringField(r.body(), "employeeCode");
        World.password = password;
    }

    public static void registerBus() {
        World.plate = Api.uniquePlate();
        var r = Api.post("/api/v1/bus-units", """
                {"plateNumber":"%s","route":"R-BDD","latitude":-12.0464,"longitude":-77.0428}
                """.formatted(World.plate));
        assertEquals(201, r.statusCode(), () -> "No se pudo registrar la unidad: " + r.body());
        World.busId = Json.longField(r.body(), "id");
    }

    public static void registerBusWithoutGps() {
        World.plate = Api.uniquePlate();
        var r = Api.post("/api/v1/bus-units", """
                {"plateNumber":"%s","route":"R-BDD"}
                """.formatted(World.plate));
        assertEquals(201, r.statusCode(), () -> "No se pudo registrar la unidad: " + r.body());
        World.busId = Json.longField(r.body(), "id");
    }

    public static void registerBusAssignedToEmployee() {
        World.plate = Api.uniquePlate();
        var r = Api.post("/api/v1/bus-units", """
                {"plateNumber":"%s","route":"R-BDD","assignedEmployeeId":%d}
                """.formatted(World.plate, World.employeeId));
        assertEquals(201, r.statusCode(), () -> "No se pudo registrar la unidad: " + r.body());
        World.busId = Json.longField(r.body(), "id");
    }

    public static HttpResponse<String> startShift(long employeeId, long busId) {
        return Api.post("/api/v1/shifts", """
                {"employeeId":%d,"busUnitId":%d,"routeOrigin":"Villa El Salvador","routeDestination":"Miraflores"}
                """.formatted(employeeId, busId));
    }

    public static void startShiftForCurrentDriver() {
        var r = startShift(World.employeeId, World.busId);
        assertEquals(201, r.statusCode(), () -> "No se pudo iniciar el servicio: " + r.body());
        World.shiftId = Json.longField(r.body(), "id");
    }
}
