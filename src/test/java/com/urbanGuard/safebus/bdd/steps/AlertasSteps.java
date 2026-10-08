package com.urbanGuard.safebus.bdd.steps;

import com.urbanGuard.safebus.bdd.support.Api;
import com.urbanGuard.safebus.bdd.support.Json;
import com.urbanGuard.safebus.bdd.support.World;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlertasSteps {

    private static String alertJson(String tipo, boolean conUbicacion) {
        String ubicacion = conUbicacion ? ",\"latitude\":-12.0464,\"longitude\":-77.0428" : "";
        return "{\"employeeId\":%d,\"busUnitId\":%d,\"alertType\":\"%s\",\"description\":\"Alerta BDD\"%s}"
                .formatted(World.employeeId, World.busId, tipo, ubicacion);
    }

    private static int alertasAlmacenadas() {
        return Json.count(Api.get("/api/v1/alerts").body(), "\"alertType\"");
    }

    // ------------------------- Envío -------------------------

    @Cuando("el conductor activa una alerta de emergencia de tipo {string} con ubicación")
    public void activaAlertaConUbicacion(String tipo) {
        World.response = Api.post("/api/v1/alerts", alertJson(tipo, true));
        if (World.response.statusCode() == 201) World.alertId = Json.longField(World.response.body(), "id");
    }

    @Cuando("el conductor activa una alerta de emergencia de tipo {string} sin ubicación")
    public void activaAlertaSinUbicacion(String tipo) {
        World.response = Api.post("/api/v1/alerts", alertJson(tipo, false));
        if (World.response.statusCode() == 201) World.alertId = Json.longField(World.response.body(), "id");
    }

    @Dado("el conductor ha registrado una alerta de tipo {string}")
    public void alertaRegistrada(String tipo) {
        World.response = Api.post("/api/v1/alerts", alertJson(tipo, true));
        assertEquals(201, World.response.statusCode(), World.response.body());
        World.alertId = Json.longField(World.response.body(), "id");
    }

    @Cuando("se envía una alerta sin tipo de alerta")
    public void alertaSinTipo() {
        World.response = Api.post("/api/v1/alerts",
                "{\"employeeId\":%d,\"busUnitId\":%d}".formatted(World.employeeId, World.busId));
    }

    @Cuando("se envía una alerta sin conductor")
    public void alertaSinConductor() {
        World.response = Api.post("/api/v1/alerts",
                "{\"busUnitId\":%d,\"alertType\":\"PANIC\"}".formatted(World.busId));
    }

    // ------------------------- Almacenamiento -------------------------

    @Dado("se cuenta el número de alertas almacenadas")
    public void cuentaAlertas() {
        World.countBefore = alertasAlmacenadas();
    }

    @Entonces("el número de alertas almacenadas no ha cambiado")
    public void alertasNoCambian() {
        assertEquals(World.countBefore, alertasAlmacenadas());
    }

    @Cuando("se consulta la alerta registrada")
    public void consultaAlerta() {
        World.response = Api.get("/api/v1/alerts/" + World.alertId);
    }

    @Cuando("se consulta una alerta inexistente")
    public void consultaAlertaInexistente() {
        World.response = Api.get("/api/v1/alerts/999999");
    }

    // ------------------------- Ubicación -------------------------

    @Entonces("la alerta guarda las coordenadas {string}, {string}")
    public void alertaGuardaCoordenadas(String lat, String lon) {
        String body = World.response.body();
        assertTrue(body.contains("\"latitude\":" + lat), body);
        assertTrue(body.contains("\"longitude\":" + lon), body);
    }

    @Entonces("la alerta queda sin datos de ubicación")
    public void alertaSinUbicacion() {
        String body = World.response.body();
        assertFalse(Json.hasNumber(body, "latitude"), body);
        assertFalse(Json.hasNumber(body, "longitude"), body);
    }

    // ------------------------- Historial -------------------------

    @Cuando("la central consulta el historial de alertas del conductor")
    public void historialDelConductor() {
        World.response = Api.get("/api/v1/alerts/employee/" + World.employeeId);
    }

    @Entonces("el historial contiene {int} alerta(s)")
    public void historialContiene(int cantidad) {
        assertEquals(cantidad, Json.count(World.response.body(), "\"alertType\""), World.response.body());
    }

    @Entonces("el historial está vacío")
    public void historialVacio() {
        assertEquals("[]", World.response.body().trim());
    }

    // ------------------------- Resolución -------------------------

    @Cuando("la central resuelve la alerta")
    public void resuelveAlerta() {
        World.response = Api.patch("/api/v1/alerts/" + World.alertId + "/resolve", "");
    }

    @Cuando("la central intenta resolver una alerta inexistente")
    public void resuelveAlertaInexistente() {
        World.response = Api.patch("/api/v1/alerts/999999/resolve", "");
    }
}
