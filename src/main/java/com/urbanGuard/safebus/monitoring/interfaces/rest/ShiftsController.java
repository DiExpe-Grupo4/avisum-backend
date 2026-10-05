package com.urbanGuard.safebus.monitoring.interfaces.rest;

import com.urbanGuard.safebus.monitoring.application.commandservices.ShiftCommandService;
import com.urbanGuard.safebus.monitoring.application.queryservices.ShiftQueryService;
import com.urbanGuard.safebus.monitoring.domain.model.commands.EndShiftCommand;
import com.urbanGuard.safebus.monitoring.domain.model.queries.GetActiveShiftByBusUnitQuery;
import com.urbanGuard.safebus.monitoring.domain.model.queries.GetAllShiftsQuery;
import com.urbanGuard.safebus.monitoring.domain.model.queries.GetShiftByIdQuery;
import com.urbanGuard.safebus.monitoring.domain.model.queries.GetShiftsByEmployeeQuery;
import com.urbanGuard.safebus.monitoring.interfaces.rest.resources.EndShiftResource;
import com.urbanGuard.safebus.monitoring.interfaces.rest.resources.ShiftResource;
import com.urbanGuard.safebus.monitoring.interfaces.rest.resources.StartShiftResource;
import com.urbanGuard.safebus.monitoring.interfaces.rest.transform.ShiftResourceFromEntityAssembler;
import com.urbanGuard.safebus.monitoring.interfaces.rest.transform.StartShiftCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.urbanGuard.safebus.monitoring.domain.model.commands.UpdateShiftProgressCommand;
import com.urbanGuard.safebus.monitoring.interfaces.rest.resources.UpdateShiftProgressResource;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/shifts", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Shifts", description = "Turnos: conductor operando una unidad de bus")
public class ShiftsController {

    private final ShiftCommandService commandService;
    private final ShiftQueryService queryService;

    public ShiftsController(ShiftCommandService commandService, ShiftQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @Operation(summary = "Iniciar turno")
    @PostMapping
    public ResponseEntity<?> startShift(@Valid @RequestBody StartShiftResource resource) {
        var result = commandService.handle(StartShiftCommandFromResourceAssembler.toCommandFromResource(resource));
        if (result.isErr()) return ResponseEntity.status(HttpStatus.CONFLICT).body(result.error());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ShiftResourceFromEntityAssembler.toResourceFromEntity(result.value()));
    }

    @Operation(summary = "Finalizar turno")
    @PatchMapping("/{id}/end")
    public ResponseEntity<?> endShift(@PathVariable Long id, @RequestBody EndShiftResource resource) {
        var command = new EndShiftCommand(id, resource.distanceKm(), resource.durationSeconds(),
                resource.passengerCount(), resource.fareCollected());
        var result = commandService.handle(command);
        if (result.isErr()) return ResponseEntity.status(HttpStatus.CONFLICT).body(result.error());
        return ResponseEntity.ok(ShiftResourceFromEntityAssembler.toResourceFromEntity(result.value()));
    }
    @Operation(summary = "Actualizar el progreso de un turno activo (vista en vivo)")
    @PatchMapping("/{id}/progress")
    public ResponseEntity<?> updateProgress(@PathVariable Long id, @RequestBody UpdateShiftProgressResource resource) {
        var command = new UpdateShiftProgressCommand(id, resource.distanceKm(), resource.durationSeconds(),
                resource.passengerCount(), resource.fareCollected());
        var result = commandService.handle(command);
        if (result.isErr()) return ResponseEntity.status(HttpStatus.CONFLICT).body(result.error());
        return ResponseEntity.ok(ShiftResourceFromEntityAssembler.toResourceFromEntity(result.value()));
    }
    @Operation(summary = "Obtener todos los turnos")
    @GetMapping
    public ResponseEntity<List<ShiftResource>> getAllShifts() {
        var list = queryService.handle(new GetAllShiftsQuery()).stream()
                .map(ShiftResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Obtener turno por ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getShiftById(@PathVariable Long id) {
        var shift = queryService.handle(new GetShiftByIdQuery(id));
        if (shift.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ShiftResourceFromEntityAssembler.toResourceFromEntity(shift.get()));
    }

    @Operation(summary = "Obtener historial de turnos de un empleado")
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<ShiftResource>> getShiftsByEmployee(@PathVariable Long employeeId) {
        var list = queryService.handle(new GetShiftsByEmployeeQuery(employeeId)).stream()
                .map(ShiftResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Obtener el turno activo de una unidad de bus")
    @GetMapping("/bus-unit/{busUnitId}/active")
    public ResponseEntity<?> getActiveShiftByBusUnit(@PathVariable Long busUnitId) {
        var shift = queryService.handle(new GetActiveShiftByBusUnitQuery(busUnitId));
        if (shift.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ShiftResourceFromEntityAssembler.toResourceFromEntity(shift.get()));
    }
}