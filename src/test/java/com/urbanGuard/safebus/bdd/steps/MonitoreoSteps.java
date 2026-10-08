package com.urbanGuard.safebus.bdd.steps;

import com.urbanGuard.safebus.bdd.support.Api;
import com.urbanGuard.safebus.bdd.support.Fixtures;
import com.urbanGuard.safebus.bdd.support.Json;
import com.urbanGuard.safebus.bdd.support.World;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MonitoreoSteps {

    // ------------------------- Conteo de pasajeros -------------------------

    @Cuando("el sistema registra una lectura de pasajeros con {int} subidas, {int} bajadas y {int} a bordo")
    public void registraLectura(int subidas, int bajadas, int abordo) {
        World.response = Api.post("/api/v1/passenger-counts",
                ("{\"shiftId\":%d,\"busUnitId\":%d,\"totalBoarded\":%d,\"totalAlighted\":%d,\"totalAboard\":%d}")
                        .formatted(World.shiftId, World.busId, subidas, bajadas, abordo));
        pausaCorta(); // evita empates en la marca de tiempo entre lecturas consecutivas
    }

    @Cuando("el sistema registra una lectura de pasajeros para un servicio inexistente")
    public void lecturaServicioInexistente() {
        World.response = Api.post("/api/v1/passenger-counts",
                ("{\"shiftId\":999999,\"busUnitId\":%d,\"totalBoarded\":5,\"totalAlighted\":0,\"totalAboard\":5}")
                        .formatted(World.busId));
    }

    @Cuando("el sistema registra una lectura sin datos de conteo")
    public void lecturaSinDatos() {
        World.response = Api.post("/api/v1/passenger-counts",
                "{\"shiftId\":%d,\"busUnitId\":%d}".formatted(World.shiftId, World.busId));
    }

    @Entonces("el servicio tiene {int} lectura(s) de pasajeros")
    public void servicioTieneLecturas(int cantidad) {
        var r = Api.get("/api/v1/passenger-counts/shift/" + World.shiftId);
        assertEquals(200, r.statusCode());
        assertEquals(cantidad, Json.count(r.body(), "\"totalAboard\""), r.body());
    }

    @Entonces("la ocupación actual de la unidad es {int} pasajeros")
    public void ocupacionActual(int pasajeros) {
        var r = Api.get("/api/v1/bus-units/" + World.busId);
        assertTrue(r.body().contains("\"currentPassengerCount\":" + pasajeros), r.body());
    }

    // ------------------------- Consulta de unidades -------------------------

    @Cuando("se consulta la información de la unidad")
    public void consultaUnidad() {
        World.response = Api.get("/api/v1/bus-units/" + World.busId);
    }

    @Cuando("se consulta una unidad inexistente")
    public void consultaUnidadInexistente() {
        World.response = Api.get("/api/v1/bus-units/999999");
    }

    @Entonces("la ocupación actual es {int} pasajeros")
    public void ocupacionEnRespuesta(int pasajeros) {
        assertTrue(World.response.body().contains("\"currentPassengerCount\":" + pasajeros), World.response.body());
    }

    @Entonces("no hay información de ocupación disponible")
    public void sinOcupacion() {
        assertFalse(Json.hasNumber(World.response.body(), "currentPassengerCount"), World.response.body());
    }

    @Cuando("la empresa consulta el estado de las unidades")
    public void consultaEstadoUnidades() {
        World.response = Api.get("/api/v1/bus-units");
    }

    @Entonces("la lista incluye la unidad registrada con estado {string}")
    public void listaIncluyeUnidad(String estado) {
        String body = World.response.body();
        int pos = body.indexOf("\"plateNumber\":\"" + World.plate + "\"");
        assertTrue(pos >= 0, "La unidad " + World.plate + " no está en la lista");
        String fragmento = body.substring(pos, Math.min(body.length(), pos + 150));
        assertTrue(fragmento.contains("\"status\":\"" + estado + "\""), fragmento);
    }

    // ------------------------- Ubicación -------------------------

    @Dado("una unidad de bus registrada sin datos de GPS")
    public void unidadSinGps() {
        Fixtures.registerBusWithoutGps();
    }

    @Cuando("la unidad reporta su ubicación {string}, {string}")
    public void reportaUbicacion(String lat, String lon) {
        World.response = Api.patch("/api/v1/bus-units/" + World.busId + "/location",
                "{\"latitude\":%s,\"longitude\":%s,\"speed\":40.5}".formatted(lat, lon));
        assertEquals(200, World.response.statusCode(), World.response.body());
    }

    @Entonces("el sistema retorna las coordenadas {string}, {string}")
    public void retornaCoordenadas(String lat, String lon) {
        String body = World.response.body();
        assertTrue(body.contains("\"currentLatitude\":" + lat), body);
        assertTrue(body.contains("\"currentLongitude\":" + lon), body);
    }

    @Entonces("el sistema indica que la ubicación no está disponible")
    public void ubicacionNoDisponible() {
        assertFalse(Json.hasNumber(World.response.body(), "currentLatitude"), World.response.body());
        assertFalse(Json.hasNumber(World.response.body(), "currentLongitude"), World.response.body());
    }

    // ------------------------- Asignación y registro de unidades -------------------------

    @Dado("una unidad de bus asignada al conductor")
    public void unidadAsignada() {
        Fixtures.registerBusAssignedToEmployee();
    }

    @Entonces("la unidad muestra al conductor asignado")
    public void muestraConductorAsignado() {
        String body = World.response.body();
        assertTrue(body.contains("\"assignedEmployeeId\":" + World.employeeId), body);
        assertTrue(body.contains("\"assignedEmployeeName\":\"Empleado Test\""), body);
    }

    @Cuando("la empresa registra una unidad nueva")
    public void registraUnidadNueva() {
        World.plate = Api.uniquePlate();
        World.response = Api.post("/api/v1/bus-units",
                "{\"plateNumber\":\"%s\",\"route\":\"R-99\"}".formatted(World.plate));
    }

    @Cuando("la empresa registra otra unidad con la misma placa")
    public void registraUnidadMismaPlaca() {
        World.response = Api.post("/api/v1/bus-units",
                "{\"plateNumber\":\"%s\",\"route\":\"R-99\"}".formatted(World.plate));
    }

    private static void pausaCorta() {
        try { Thread.sleep(25); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
