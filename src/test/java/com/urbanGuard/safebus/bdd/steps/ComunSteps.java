package com.urbanGuard.safebus.bdd.steps;

import com.urbanGuard.safebus.bdd.support.World;
import io.cucumber.java.es.Entonces;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ComunSteps {

    @Entonces("el sistema responde con código {int}")
    public void respondeConCodigo(int codigo) {
        assertEquals(codigo, World.response.statusCode(),
                () -> "Respuesta inesperada. Body: " + World.response.body());
    }

    @Entonces("la respuesta contiene {string}")
    public void respuestaContiene(String texto) {
        assertTrue(World.response.body().contains(texto),
                () -> "El body no contiene [" + texto + "]. Body: " + World.response.body());
    }

    @Entonces("la respuesta no contiene {string}")
    public void respuestaNoContiene(String texto) {
        assertFalse(World.response.body().contains(texto),
                () -> "El body no debería contener [" + texto + "]. Body: " + World.response.body());
    }

    @Entonces("el estado registrado es {string}")
    public void estadoRegistrado(String estado) {
        assertTrue(World.response.body().contains("\"status\":\"" + estado + "\""),
                () -> "Se esperaba estado " + estado + ". Body: " + World.response.body());
    }
}
