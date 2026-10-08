package com.urbanGuard.safebus.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Core Integration: EmployeesController")
class EmployeesApiIntegrationTest extends AbstractApiIntegrationTest {

    // ------------------------------ POST /api/v1/employees ------------------------------

    @Test
    @DisplayName("POST /employees con datos válidos devuelve 201 y no expone la contraseña")
    void createEmployee_valid_returns201() {
        var response = createEmployee("clave123");

        assertStatus(201, response);
        assertBodyContains(response, "\"role\":\"CONDUCTOR\"");
        assertBodyContains(response, "\"active\":true");
        assertBodyContains(response, "\"employeeCode\":\"");
        org.junit.jupiter.api.Assertions.assertFalse(response.body().contains("clave123"),
                "La respuesta no debe contener la contraseña");
    }

    @Test
    @DisplayName("POST /employees con email inválido devuelve 400")
    void createEmployee_invalidEmail_returns400() {
        var response = post("/api/v1/employees", """
                {"fullName":"Test","email":"no-es-email","password":"x","role":"CONDUCTOR","dni":"%s"}
                """.formatted(uniqueDni()));

        assertStatus(400, response);
    }

    @Test
    @DisplayName("POST /employees sin nombre devuelve 400")
    void createEmployee_missingFullName_returns400() {
        var response = post("/api/v1/employees", """
                {"email":"%s","password":"x","role":"CONDUCTOR","dni":"%s"}
                """.formatted(uniqueEmail(), uniqueDni()));

        assertStatus(400, response);
    }

    @Test
    @DisplayName("POST /employees con DNI de 7 dígitos devuelve 400")
    void createEmployee_shortDni_returns400() {
        var response = post("/api/v1/employees", """
                {"fullName":"Test","email":"%s","password":"x","role":"CONDUCTOR","dni":"1234567"}
                """.formatted(uniqueEmail()));

        assertStatus(400, response);
    }

    @Test
    @DisplayName("POST /employees con email repetido devuelve 409")
    void createEmployee_duplicateEmail_returns409() {
        String email = uniqueEmail();
        String body = """
                {"fullName":"Test","email":"%s","password":"x","role":"CONDUCTOR","dni":"%s"}
                """;
        assertStatus(201, post("/api/v1/employees", body.formatted(email, uniqueDni())));

        var duplicate = post("/api/v1/employees", body.formatted(email, uniqueDni()));

        assertStatus(409, duplicate);
        assertBodyContains(duplicate, "Email already in use");
    }

    @Test
    @DisplayName("POST /employees con DNI repetido devuelve 409")
    void createEmployee_duplicateDni_returns409() {
        String dni = uniqueDni();
        String body = """
                {"fullName":"Test","email":"%s","password":"x","role":"CONDUCTOR","dni":"%s"}
                """;
        assertStatus(201, post("/api/v1/employees", body.formatted(uniqueEmail(), dni)));

        var duplicate = post("/api/v1/employees", body.formatted(uniqueEmail(), dni));

        assertStatus(409, duplicate);
        assertBodyContains(duplicate, "DNI already in use");
    }

    // ------------------------------ GET ------------------------------

    @Test
    @DisplayName("GET /employees devuelve 200 con la lista (incluye los datos semilla)")
    void getAllEmployees_returns200() {
        var response = get("/api/v1/employees");

        assertStatus(200, response);
        assertBodyContains(response, "EMP-001");
    }

    @Test
    @DisplayName("GET /employees/{id} existente devuelve 200")
    void getEmployeeById_existing_returns200() {
        long id = createEmployeeId();

        var response = get("/api/v1/employees/" + id);

        assertStatus(200, response);
        assertBodyContains(response, "\"id\":" + id);
    }

    @Test
    @DisplayName("GET /employees/{id} inexistente devuelve 404")
    void getEmployeeById_missing_returns404() {
        assertStatus(404, get("/api/v1/employees/999999"));
    }

    @Test
    @DisplayName("GET /employees/code/{code} existente devuelve 200")
    void getEmployeeByCode_existing_returns200() {
        var response = get("/api/v1/employees/code/EMP-001");

        assertStatus(200, response);
        assertBodyContains(response, "\"employeeCode\":\"EMP-001\"");
    }

    @Test
    @DisplayName("GET /employees/code/{code} inexistente devuelve 404")
    void getEmployeeByCode_missing_returns404() {
        assertStatus(404, get("/api/v1/employees/code/NO-EXISTE"));
    }

    // ------------------------------ PUT / PATCH / DELETE ------------------------------

    @Test
    @DisplayName("PUT /employees/{id} actualiza los datos y devuelve 200")
    void updateEmployee_valid_returns200() {
        long id = createEmployeeId();

        var response = put("/api/v1/employees/" + id, """
                {"fullName":"Nombre Actualizado","email":"%s","role":"ADMIN","dni":"%s"}
                """.formatted(uniqueEmail(), uniqueDni()));

        assertStatus(200, response);
        assertBodyContains(response, "\"fullName\":\"Nombre Actualizado\"");
        assertBodyContains(response, "\"role\":\"ADMIN\"");
    }

    @Test
    @DisplayName("PUT /employees/{id} inexistente devuelve 409 (comportamiento actual del controlador)")
    void updateEmployee_missing_returns409() {
        var response = put("/api/v1/employees/999999", """
                {"fullName":"X","email":"%s","role":"ADMIN","dni":"%s"}
                """.formatted(uniqueEmail(), uniqueDni()));

        assertStatus(409, response);
        assertBodyContains(response, "Empleado no encontrado");
    }

    @Test
    @DisplayName("PATCH /employees/{id}/deactivate y /reactivate cambian el estado activo")
    void deactivateAndReactivate_returns200() {
        long id = createEmployeeId();

        var deactivated = patch("/api/v1/employees/" + id + "/deactivate", "");
        assertStatus(200, deactivated);
        assertBodyContains(deactivated, "\"active\":false");

        var reactivated = patch("/api/v1/employees/" + id + "/reactivate", "");
        assertStatus(200, reactivated);
        assertBodyContains(reactivated, "\"active\":true");
    }

    @Test
    @DisplayName("PATCH /employees/{id}/deactivate inexistente devuelve 404")
    void deactivate_missing_returns404() {
        assertStatus(404, patch("/api/v1/employees/999999/deactivate", ""));
    }

    @Test
    @DisplayName("DELETE /employees/{id} devuelve 204 (soft delete)")
    void deleteEmployee_existing_returns204() {
        long id = createEmployeeId();

        assertStatus(204, delete("/api/v1/employees/" + id));
    }

    @Test
    @DisplayName("DELETE /employees/{id} inexistente devuelve 404")
    void deleteEmployee_missing_returns404() {
        assertStatus(404, delete("/api/v1/employees/999999"));
    }

    // ------------------------------ POST /login ------------------------------

    private String loginBody(String code, String password) {
        return "{\"employeeCode\":\"%s\",\"password\":\"%s\"}".formatted(code, password);
    }

    private String codeOf(long id) {
        var body = get("/api/v1/employees/" + id).body();
        var m = java.util.regex.Pattern.compile("\"employeeCode\"\\s*:\\s*\"([^\"]+)\"").matcher(body);
        org.junit.jupiter.api.Assertions.assertTrue(m.find(), body);
        return m.group(1);
    }

    @Test
    @DisplayName("POST /employees/login con credenciales correctas devuelve 200")
    void login_validCredentials_returns200() {
        long id = createEmployeeId();

        var response = post("/api/v1/employees/login", loginBody(codeOf(id), "clave123"));

        assertStatus(200, response);
        assertBodyContains(response, "\"id\":" + id);
    }

    @Test
    @DisplayName("POST /employees/login con contraseña incorrecta devuelve 401")
    void login_wrongPassword_returns401() {
        long id = createEmployeeId();

        var response = post("/api/v1/employees/login", loginBody(codeOf(id), "incorrecta"));

        assertStatus(401, response);
        assertBodyContains(response, "Credenciales inválidas");
    }

    @Test
    @DisplayName("POST /employees/login con código inexistente devuelve 401")
    void login_unknownCode_returns401() {
        assertStatus(401, post("/api/v1/employees/login", loginBody("NO-EXISTE", "x")));
    }

    @Test
    @DisplayName("POST /employees/login sin contraseña devuelve 400")
    void login_missingPassword_returns400() {
        assertStatus(400, post("/api/v1/employees/login", "{\"employeeCode\":\"EMP-001\"}"));
    }

    @Test
    @DisplayName("POST /employees/login de un empleado desactivado devuelve 401")
    void login_deactivatedEmployee_returns401() {
        long id = createEmployeeId();
        String code = codeOf(id);
        assertStatus(200, patch("/api/v1/employees/" + id + "/deactivate", ""));

        var response = post("/api/v1/employees/login", loginBody(code, "clave123"));

        assertStatus(401, response);
        assertBodyContains(response, "desactivado");
    }

    @Test
    @DisplayName("Tras 5 intentos fallidos la cuenta se bloquea aunque la contraseña sea correcta")
    void login_afterFiveFailures_accountIsLocked() {
        long id = createEmployeeId();
        String code = codeOf(id);

        for (int i = 0; i < 5; i++) {
            assertStatus(401, post("/api/v1/employees/login", loginBody(code, "mala")));
        }
        var response = post("/api/v1/employees/login", loginBody(code, "clave123"));

        assertStatus(401, response);
        assertBodyContains(response, "bloqueada");
    }
}
