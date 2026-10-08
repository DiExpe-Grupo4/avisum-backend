package com.urbanGuard.safebus.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Base de las Core Integration Tests.
 * Levanta la aplicación completa (controlador + servicio + repositorio + base H2 en memoria)
 * en un puerto aleatorio y expone helpers para llamar a la API REST real por HTTP.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractApiIntegrationTest {

    private static final AtomicInteger SEQ = new AtomicInteger(1000);
    private static final Pattern ID_PATTERN = Pattern.compile("\"id\"\\s*:\\s*(\\d+)");

    @Value("${local.server.port}")
    private int port;

    private final HttpClient client = HttpClient.newHttpClient();

    // ---------- datos únicos para que los tests no choquen entre sí ----------

    protected static int next() {
        return SEQ.incrementAndGet();
    }

    /** DNI único de 8 dígitos (los DNI de los seeders empiezan en 1000000x). */
    protected static String uniqueDni() {
        return String.format("%08d", 20_000_000 + next());
    }

    protected static String uniqueEmail() {
        return "it" + next() + "@test.safebus.com";
    }

    protected static String uniquePlate() {
        return "IT-" + next();
    }

    // ---------- helpers HTTP ----------

    protected HttpResponse<String> get(String path) {
        return send(HttpRequest.newBuilder(uri(path)).GET());
    }

    protected HttpResponse<String> post(String path, String json) {
        return send(HttpRequest.newBuilder(uri(path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json)));
    }

    protected HttpResponse<String> put(String path, String json) {
        return send(HttpRequest.newBuilder(uri(path))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json)));
    }

    protected HttpResponse<String> patch(String path, String json) {
        return send(HttpRequest.newBuilder(uri(path))
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json)));
    }

    protected HttpResponse<String> delete(String path) {
        return send(HttpRequest.newBuilder(uri(path)).DELETE());
    }

    private HttpResponse<String> send(HttpRequest.Builder builder) {
        try {
            return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new IllegalStateException("Error llamando a la API: " + e.getMessage(), e);
        }
    }

    private URI uri(String path) {
        return URI.create("http://localhost:" + port + path);
    }

    // ---------- aserciones y utilidades ----------

    protected void assertStatus(int expected, HttpResponse<String> response) {
        assertEquals(expected, response.statusCode(),
                () -> "HTTP " + response.statusCode() + " inesperado. Body: " + response.body());
    }

    protected void assertBodyContains(HttpResponse<String> response, String expected) {
        assertTrue(response.body().contains(expected),
                () -> "El body no contiene [" + expected + "]. Body: " + response.body());
    }

    /** Extrae el primer "id" numérico del JSON de respuesta. */
    protected long idOf(HttpResponse<String> response) {
        Matcher m = ID_PATTERN.matcher(response.body());
        assertTrue(m.find(), () -> "No se encontró 'id' en: " + response.body());
        return Long.parseLong(m.group(1));
    }

    // ---------- creación de datos de apoyo vía API ----------

    protected HttpResponse<String> createEmployee(String password) {
        return post("/api/v1/employees", """
                {"fullName":"Empleado Test","email":"%s","password":"%s","role":"CONDUCTOR","dni":"%s"}
                """.formatted(uniqueEmail(), password, uniqueDni()));
    }

    protected long createEmployeeId() {
        var response = createEmployee("clave123");
        assertStatus(201, response);
        return idOf(response);
    }

    protected long createBusUnitId() {
        var response = post("/api/v1/bus-units", """
                {"plateNumber":"%s","route":"R-TEST","latitude":-12.0464,"longitude":-77.0428}
                """.formatted(uniquePlate()));
        assertStatus(201, response);
        return idOf(response);
    }
}
