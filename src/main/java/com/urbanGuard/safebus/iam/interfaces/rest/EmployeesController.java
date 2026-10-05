package com.urbanGuard.safebus.iam.interfaces.rest;

import com.urbanGuard.safebus.iam.application.commandservices.EmployeeCommandService;
import com.urbanGuard.safebus.iam.application.queryservices.EmployeeQueryService;
import com.urbanGuard.safebus.iam.domain.model.commands.DeactivateEmployeeCommand;
import com.urbanGuard.safebus.iam.domain.model.commands.DeleteEmployeeCommand;
import com.urbanGuard.safebus.iam.domain.model.commands.ReactivateEmployeeCommand;
import com.urbanGuard.safebus.iam.domain.model.queries.GetAllEmployeesQuery;
import com.urbanGuard.safebus.iam.domain.model.queries.GetEmployeeByCodeQuery;
import com.urbanGuard.safebus.iam.domain.model.queries.GetEmployeeByIdQuery;
import com.urbanGuard.safebus.iam.interfaces.rest.resources.CreateEmployeeResource;
import com.urbanGuard.safebus.iam.interfaces.rest.resources.EmployeeResource;
import com.urbanGuard.safebus.iam.interfaces.rest.resources.LoginResource;
import com.urbanGuard.safebus.iam.interfaces.rest.resources.UpdateEmployeeResource;
import com.urbanGuard.safebus.iam.interfaces.rest.transform.CreateEmployeeCommandFromResourceAssembler;
import com.urbanGuard.safebus.iam.interfaces.rest.transform.EmployeeResourceFromEntityAssembler;
import com.urbanGuard.safebus.iam.interfaces.rest.transform.LoginCommandFromResourceAssembler;
import com.urbanGuard.safebus.iam.interfaces.rest.transform.UpdateEmployeeCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/employees", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Employees", description = "Endpoints para autenticación y gestión de empleados")
public class EmployeesController {

    private final EmployeeCommandService employeeCommandService;
    private final EmployeeQueryService employeeQueryService;

    public EmployeesController(EmployeeCommandService employeeCommandService, EmployeeQueryService employeeQueryService) {
        this.employeeCommandService = employeeCommandService;
        this.employeeQueryService = employeeQueryService;
    }

    @Operation(summary = "Crear empleado")
    @PostMapping
    public ResponseEntity<?> createEmployee(@Valid @RequestBody CreateEmployeeResource resource) {
        var result = employeeCommandService.handle(
                CreateEmployeeCommandFromResourceAssembler.toCommandFromResource(resource));
        if (result.isErr()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result.error());
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(EmployeeResourceFromEntityAssembler.toResourceFromEntity(result.value()));
    }

    @Operation(summary = "Editar datos de un empleado")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @Valid @RequestBody UpdateEmployeeResource resource) {
        var result = employeeCommandService.handle(
                UpdateEmployeeCommandFromResourceAssembler.toCommandFromResource(id, resource));
        if (result.isErr()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result.error());
        }
        return ResponseEntity.ok(EmployeeResourceFromEntityAssembler.toResourceFromEntity(result.value()));
    }

    @Operation(summary = "Desactivar empleado (no puede iniciar sesión, no se borra)")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateEmployee(@PathVariable Long id) {
        var result = employeeCommandService.handle(new DeactivateEmployeeCommand(id));
        if (result.isErr()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.error());
        }
        return ResponseEntity.ok(EmployeeResourceFromEntityAssembler.toResourceFromEntity(result.value()));
    }

    @Operation(summary = "Reactivar empleado desactivado")
    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<?> reactivateEmployee(@PathVariable Long id) {
        var result = employeeCommandService.handle(new ReactivateEmployeeCommand(id));
        if (result.isErr()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.error());
        }
        return ResponseEntity.ok(EmployeeResourceFromEntityAssembler.toResourceFromEntity(result.value()));
    }

    @Operation(summary = "Eliminar empleado (soft delete, conserva historial de turnos)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        var result = employeeCommandService.handle(new DeleteEmployeeCommand(id));
        if (result.isErr()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.error());
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Login de empleado (valida password y aplica bloqueo por intentos fallidos)")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginResource resource) {
        var result = employeeCommandService.handle(
                LoginCommandFromResourceAssembler.toCommandFromResource(resource));
        if (result.isErr()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result.error());
        }
        return ResponseEntity.ok(EmployeeResourceFromEntityAssembler.toResourceFromEntity(result.value()));
    }

    @Operation(summary = "Obtener todos los empleados")
    @GetMapping
    public ResponseEntity<List<EmployeeResource>> getAllEmployees() {
        var employees = employeeQueryService.handle(new GetAllEmployeesQuery());
        var resources = employees.stream()
                .map(EmployeeResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @Operation(summary = "Obtener empleado por ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id) {
        var employee = employeeQueryService.handle(new GetEmployeeByIdQuery(id));
        if (employee.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(EmployeeResourceFromEntityAssembler.toResourceFromEntity(employee.get()));
    }

    @Operation(summary = "Obtener empleado por código (login)")
    @GetMapping("/code/{employeeCode}")
    public ResponseEntity<?> getEmployeeByCode(@PathVariable String employeeCode) {
        var employee = employeeQueryService.handle(new GetEmployeeByCodeQuery(employeeCode));
        if (employee.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(EmployeeResourceFromEntityAssembler.toResourceFromEntity(employee.get()));
    }
}