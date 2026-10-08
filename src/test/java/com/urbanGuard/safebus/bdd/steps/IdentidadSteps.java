package com.urbanGuard.safebus.bdd.steps;

import com.urbanGuard.safebus.bdd.support.Api;
import com.urbanGuard.safebus.bdd.support.Fixtures;
import com.urbanGuard.safebus.bdd.support.World;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IdentidadSteps {

    private static String loginJson(String codigo, String password) {
        return "{\"employeeCode\":\"%s\",\"password\":\"%s\"}".formatted(codigo, password);
    }

    @Dado("un conductor registrado con contraseña {string}")
    public void conductorConContrasena(String password) {
        Fixtures.registerEmployee(password);
    }

    @Cuando("el conductor inicia sesión con su código y la contraseña {string}")
    public void iniciaSesionConSuCodigo(String password) {
        World.response = Api.post("/api/v1/employees/login", loginJson(World.employeeCode, password));
    }

    @Cuando("el conductor inicia sesión con el código {string} y la contraseña {string}")
    public void iniciaSesionConCodigo(String codigo, String password) {
        World.response = Api.post("/api/v1/employees/login", loginJson(codigo, password));
    }

    @Dado("el conductor falló {int} veces el inicio de sesión")
    public void fallaVeces(int veces) {
        for (int i = 0; i < veces; i++) {
            var r = Api.post("/api/v1/employees/login", loginJson(World.employeeCode, "contraseña-incorrecta"));
            assertEquals(401, r.statusCode());
        }
    }

    @Dado("el administrador desactiva al conductor")
    public void desactivaConductor() {
        World.response = Api.patch("/api/v1/employees/" + World.employeeId + "/deactivate", "");
        assertEquals(200, World.response.statusCode());
    }

    @Dado("el administrador reactiva al conductor")
    public void reactivaConductor() {
        World.response = Api.patch("/api/v1/employees/" + World.employeeId + "/reactivate", "");
        assertEquals(200, World.response.statusCode());
    }

    // ------------------------- Verificación facial -------------------------

    @Cuando("la cámara envía una captura para verificar al conductor")
    public void enviaCaptura() {
        World.response = Api.post("/api/v1/face-verifications",
                "{\"employeeId\":%d,\"capturedImageRef\":\"captura-001.jpg\"}".formatted(World.employeeId));
    }

    @Cuando("la cámara envía una verificación sin captura")
    public void enviaSinCaptura() {
        World.response = Api.post("/api/v1/face-verifications",
                "{\"employeeId\":%d}".formatted(World.employeeId));
    }

    @Cuando("la cámara envía una captura para un conductor inexistente")
    public void enviaCapturaConductorInexistente() {
        World.response = Api.post("/api/v1/face-verifications",
                "{\"employeeId\":999999,\"capturedImageRef\":\"captura-001.jpg\"}");
    }

    @Entonces("el resultado de la verificación es MATCH o NO_MATCH con un puntaje entre 0 y 1")
    public void resultadoVerificacion() {
        String body = World.response.body();
        assertTrue(Pattern.compile("\"matchResult\"\\s*:\\s*\"(MATCH|NO_MATCH)\"").matcher(body).find(), body);
        var m = Pattern.compile("\"confidenceScore\"\\s*:\\s*([0-9.]+)").matcher(body);
        assertTrue(m.find(), body);
        double score = Double.parseDouble(m.group(1));
        assertTrue(score >= 0.0 && score <= 1.0, "Puntaje fuera de rango: " + score);
    }
}
