package com.urbanGuard.safebus.bdd.steps;

import com.urbanGuard.safebus.bdd.support.Api;
import com.urbanGuard.safebus.bdd.support.Fixtures;
import com.urbanGuard.safebus.bdd.support.Json;
import com.urbanGuard.safebus.bdd.support.World;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServicioSteps {

    private static final String FIN_JSON =
            "{\"distanceKm\":10.0,\"durationSeconds\":3600,\"passengerCount\":40,\"fareCollected\":120.0}";

    @Dado("un conductor registrado")
    public void conductorRegistrado() {
        Fixtures.registerEmployee("clave123");
    }

    @Dado("una unidad de bus registrada")
    public void unidadRegistrada() {
        Fixtures.registerBus();
    }

    @Dado("el conductor tiene un servicio en curso")
    public void conductorConServicioEnCurso() {
        Fixtures.startShiftForCurrentDriver();
    }

    /** Atajo: crea conductor, unidad y servicio activo. */
    @Dado("un servicio en curso")
    public void servicioEnCurso() {
        Fixtures.registerEmployee("clave123");
        Fixtures.registerBus();
        Fixtures.startShiftForCurrentDriver();
    }

    @Dado("el servicio ya fue finalizado")
    public void servicioYaFinalizado() {
        var r = Api.patch("/api/v1/shifts/" + World.shiftId + "/end", FIN_JSON);
        assertEquals(200, r.statusCode(), r.body());
    }

    // ------------------------- Inicio -------------------------

    @Cuando("el conductor inicia el servicio en esa unidad")
    public void iniciaServicio() {
        World.response = Fixtures.startShift(World.employeeId, World.busId);
        if (World.response.statusCode() == 201) World.shiftId = Json.longField(World.response.body(), "id");
    }

    @Cuando("un empleado inexistente intenta iniciar el servicio")
    public void empleadoInexistenteIniciaServicio() {
        World.response = Fixtures.startShift(999999L, World.busId);
    }

    @Cuando("se envía una solicitud de inicio de servicio sin unidad")
    public void inicioSinUnidad() {
        World.response = Api.post("/api/v1/shifts", "{\"employeeId\":%d}".formatted(World.employeeId));
    }

    @Cuando("otro conductor intenta iniciar servicio en la misma unidad")
    public void otroConductorMismaUnidad() {
        long busId = World.busId;
        Fixtures.registerEmployee("clave123");
        World.response = Fixtures.startShift(World.employeeId, busId);
    }

    @Cuando("el mismo conductor intenta iniciar servicio en otra unidad")
    public void mismoConductorOtraUnidad() {
        long employeeId = World.employeeId;
        Fixtures.registerBus();
        World.response = Fixtures.startShift(employeeId, World.busId);
    }

    // ------------------------- Cierre y consulta -------------------------

    @Cuando("el conductor finaliza el servicio")
    public void finalizaServicio() {
        World.response = Api.patch("/api/v1/shifts/" + World.shiftId + "/end", FIN_JSON);
    }

    @Cuando("el conductor intenta finalizar un servicio inexistente")
    public void finalizaServicioInexistente() {
        World.response = Api.patch("/api/v1/shifts/999999/end", FIN_JSON);
    }

    @Cuando("se consulta el estado del servicio")
    public void consultaEstadoServicio() {
        World.response = Api.get("/api/v1/shifts/" + World.shiftId);
    }

    @Cuando("se consulta el estado de un servicio inexistente")
    public void consultaServicioInexistente() {
        World.response = Api.get("/api/v1/shifts/999999");
    }

    // ------------------------- Verificaciones -------------------------

    @Entonces("queda registrada la hora de inicio")
    public void horaDeInicio() {
        assertTrue(Json.hasValue(World.response.body(), "startedAt"), World.response.body());
    }

    @Entonces("queda registrada la hora de cierre")
    public void horaDeCierre() {
        assertTrue(Json.hasValue(World.response.body(), "endedAt"), World.response.body());
    }
}
