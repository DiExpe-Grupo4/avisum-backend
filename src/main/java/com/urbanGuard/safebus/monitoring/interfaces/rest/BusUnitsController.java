package com.urbanGuard.safebus.monitoring.interfaces.rest;

import com.urbanGuard.safebus.iam.infrastructure.persistence.jpa.EmployeeRepository;
import com.urbanGuard.safebus.monitoring.application.commandservices.BusUnitCommandService;
import com.urbanGuard.safebus.monitoring.application.queryservices.BusUnitQueryService;
import com.urbanGuard.safebus.monitoring.domain.model.aggregates.BusUnit;
import com.urbanGuard.safebus.monitoring.domain.model.commands.UpdateBusLocationCommand;
import com.urbanGuard.safebus.monitoring.domain.model.queries.GetAllBusUnitsQuery;
import com.urbanGuard.safebus.monitoring.domain.model.queries.GetBusUnitByIdQuery;
import com.urbanGuard.safebus.monitoring.infrastructure.persistence.jpa.PassengerCountRepository;
import com.urbanGuard.safebus.monitoring.interfaces.rest.resources.BusUnitResource;
import com.urbanGuard.safebus.monitoring.interfaces.rest.resources.CreateBusUnitResource;
import com.urbanGuard.safebus.monitoring.interfaces.rest.resources.UpdateBusLocationResource;
import com.urbanGuard.safebus.monitoring.interfaces.rest.transform.BusUnitResourceFromEntityAssembler;
import com.urbanGuard.safebus.monitoring.interfaces.rest.transform.CreateBusUnitCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/bus-units", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Bus Units", description = "Monitoreo de unidades de transporte")
public class BusUnitsController {

    private final BusUnitCommandService commandService;
    private final BusUnitQueryService queryService;
    private final EmployeeRepository employeeRepository;
    private final PassengerCountRepository passengerCountRepository;

    public BusUnitsController(BusUnitCommandService commandService, BusUnitQueryService queryService,
                              EmployeeRepository employeeRepository,
                              PassengerCountRepository passengerCountRepository) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.employeeRepository = employeeRepository;
        this.passengerCountRepository = passengerCountRepository;
    }

    private BusUnitResource toEnrichedResource(BusUnit bus) {
        Long assignedEmployeeId = bus.getAssignedEmployeeId();
        String assignedEmployeeName = (assignedEmployeeId != null)
                ? employeeRepository.findById(assignedEmployeeId).map(e -> e.getFullName()).orElse(null)
                : null;

        Integer currentPassengerCount = passengerCountRepository.findFirstByBusUnitIdOrderByRecordedAtDesc(bus.getId())
                .map(p -> p.getTotalAboard())
                .orElse(null);

        return BusUnitResourceFromEntityAssembler.toResourceFromEntity(
                bus, assignedEmployeeId, assignedEmployeeName, currentPassengerCount);
    }

    @Operation(summary = "Registrar unidad de bus")
    @PostMapping
    public ResponseEntity<?> createBusUnit(@Valid @RequestBody CreateBusUnitResource resource) {
        var result = commandService.handle(CreateBusUnitCommandFromResourceAssembler.toCommandFromResource(resource));
        if (result.isErr()) return ResponseEntity.status(HttpStatus.CONFLICT).body(result.error());
        return ResponseEntity.status(HttpStatus.CREATED).body(toEnrichedResource(result.value()));
    }

    @Operation(summary = "Obtener todas las unidades")
    @GetMapping
    public ResponseEntity<List<BusUnitResource>> getAllBusUnits() {
        var list = queryService.handle(new GetAllBusUnitsQuery()).stream()
                .map(this::toEnrichedResource).toList();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Obtener unidad por ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getBusUnitById(@PathVariable Long id) {
        var bus = queryService.handle(new GetBusUnitByIdQuery(id));
        if (bus.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toEnrichedResource(bus.get()));
    }

    @Operation(summary = "Actualizar ubicación (y opcionalmente velocidad) de unidad")
    @PatchMapping("/{id}/location")
    public ResponseEntity<?> updateLocation(@PathVariable Long id, @RequestBody UpdateBusLocationResource resource) {
        var result = commandService.handle(new UpdateBusLocationCommand(id, resource.latitude(), resource.longitude(), resource.speed()));
        if (result.isErr()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toEnrichedResource(result.value()));
    }
}