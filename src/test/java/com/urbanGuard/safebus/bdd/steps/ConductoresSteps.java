package com.urbanGuard.safebus.bdd.steps;

import com.urbanGuard.safebus.bdd.support.Api;
import com.urbanGuard.safebus.bdd.support.World;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConductoresSteps {

    @Cuando("el administrador actualiza los datos del conductor con el nombre {string}")
    public void actualizaDatos(String nombre) {
        World.response = Api.put("/api/v1/employees/" + World.employeeId, """
                {"fullName":"%s","email":"%s","role":"CONDUCTOR","dni":"%s"}
                """.formatted(nombre, Api.uniqueEmail(), Api.uniqueDni()));
    }

    @Cuando("el administrador actualiza al conductor con un DNI de {int} dígitos")
    public void actualizaConDniInvalido(int digitos) {
        String dni = "123456789012".substring(0, digitos);
        World.response = Api.put("/api/v1/employees/" + World.employeeId, """
                {"fullName":"Nombre","email":"%s","role":"CONDUCTOR","dni":"%s"}
                """.formatted(Api.uniqueEmail(), dni));
    }

    @Entonces("los datos del conductor reflejan el nombre {string}")
    public void reflejaNombre(String nombre) {
        assertTrue(World.response.body().contains("\"fullName\":\"" + nombre + "\""), World.response.body());
    }
}
