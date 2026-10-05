// ShiftCommandServiceImpl.java
package com.urbanGuard.safebus.monitoring.application.internal.commandservices;

import com.urbanGuard.safebus.iam.infrastructure.persistence.jpa.EmployeeRepository;
import com.urbanGuard.safebus.monitoring.application.commandservices.ShiftCommandService;
import com.urbanGuard.safebus.monitoring.domain.model.aggregates.Shift;
import com.urbanGuard.safebus.monitoring.domain.model.commands.EndShiftCommand;
import com.urbanGuard.safebus.monitoring.domain.model.commands.StartShiftCommand;
import com.urbanGuard.safebus.monitoring.domain.model.commands.UpdateShiftProgressCommand;
import com.urbanGuard.safebus.monitoring.infrastructure.persistence.jpa.BusUnitRepository;
import com.urbanGuard.safebus.monitoring.infrastructure.persistence.jpa.ShiftRepository;
import com.urbanGuard.safebus.shared.application.result.Result;
import org.springframework.stereotype.Service;

@Service
public class ShiftCommandServiceImpl implements ShiftCommandService {

    private final ShiftRepository repo;
    private final BusUnitRepository busUnitRepo;
    private final EmployeeRepository employeeRepo;

    public ShiftCommandServiceImpl(ShiftRepository repo, BusUnitRepository busUnitRepo, EmployeeRepository employeeRepo) {
        this.repo = repo;
        this.busUnitRepo = busUnitRepo;
        this.employeeRepo = employeeRepo;
    }

    @Override
    public Result<Shift, String> handle(StartShiftCommand command) {
        if (employeeRepo.findById(command.employeeId()).isEmpty())
            return Result.err("Empleado no encontrado: " + command.employeeId());

        var busUnit = busUnitRepo.findById(command.busUnitId());
        if (busUnit.isEmpty())
            return Result.err("Unidad de bus no encontrada: " + command.busUnitId());

        if (repo.existsByBusUnitIdAndStatus(command.busUnitId(), "ACTIVE"))
            return Result.err("La unidad ya tiene un turno activo");

        if (repo.existsByEmployeeIdAndStatus(command.employeeId(), "ACTIVE"))
            return Result.err("El empleado ya tiene un turno activo");

        var shift = new Shift(command, busUnit.get().getRoute());
        repo.save(shift);
        return Result.ok(shift);
    }

    @Override
    public Result<Shift, String> handle(EndShiftCommand command) {
        var shiftOpt = repo.findById(command.shiftId());
        if (shiftOpt.isEmpty())
            return Result.err("Turno no encontrado: " + command.shiftId());

        var shift = shiftOpt.get();
        if (!shift.isActive())
            return Result.err("El turno ya fue finalizado");

        shift.finish(command.distanceKm(), command.durationSeconds(), command.passengerCount(), command.fareCollected());
        repo.save(shift);
        return Result.ok(shift);
    }

    @Override
    public Result<Shift, String> handle(UpdateShiftProgressCommand command) {
        var shiftOpt = repo.findById(command.shiftId());
        if (shiftOpt.isEmpty())
            return Result.err("Turno no encontrado: " + command.shiftId());

        var shift = shiftOpt.get();
        if (!shift.isActive())
            return Result.err("El turno ya fue finalizado");

        shift.updateProgress(command.distanceKm(), command.durationSeconds(), command.passengerCount(), command.fareCollected());
        repo.save(shift);
        return Result.ok(shift);
    }
}