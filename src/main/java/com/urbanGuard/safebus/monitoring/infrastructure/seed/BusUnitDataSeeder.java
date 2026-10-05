package com.urbanGuard.safebus.monitoring.infrastructure.seed;

import com.urbanGuard.safebus.iam.infrastructure.persistence.jpa.EmployeeRepository;
import com.urbanGuard.safebus.monitoring.domain.model.aggregates.BusUnit;
import com.urbanGuard.safebus.monitoring.domain.model.commands.CreateBusUnitCommand;
import com.urbanGuard.safebus.monitoring.infrastructure.persistence.jpa.BusUnitRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Crea las 7 unidades de bus de demo al iniciar la aplicación, solo si aún no existen.
 * Las placas coinciden 1 a 1 con los conductores semilla (EMP-001 a EMP-007), y cada
 * unidad queda asignada a su conductor mediante assignedEmployeeId.
 * Se ejecuta después de EmployeeDataSeeder (ver @Order) para que esos empleados ya existan.
 */
@Component
@Order(2)
public class BusUnitDataSeeder implements CommandLineRunner {

    private final BusUnitRepository busUnitRepository;
    private final EmployeeRepository employeeRepository;

    public BusUnitDataSeeder(BusUnitRepository busUnitRepository, EmployeeRepository employeeRepository) {
        this.busUnitRepository = busUnitRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void run(String... args) {
        seedIfMissing("ABC-1234", "R-42", -12.0464, -77.0428, "EMP-001");
        seedIfMissing("DEF-5678", "R-15", -12.0600, -77.0300, "EMP-002");
        seedIfMissing("GHI-9012", "R-07", -12.0700, -77.0500, "EMP-003");
        seedIfMissing("JKL-3456", "R-22", -12.0900, -77.0600, "EMP-004");
        seedIfMissing("MNO-7890", "R-33", -12.1000, -77.0200, "EMP-005");
        seedIfMissing("PQR-1234", "R-42", -12.0300, -77.0100, "EMP-006");
        seedIfMissing("STU-5678", "R-08", -12.0200, -77.0400, "EMP-007");
    }

    private void seedIfMissing(String plateNumber, String route, double lat, double lng, String employeeCode) {
        if (!busUnitRepository.existsByPlateNumber(plateNumber)) {
            Long employeeId = employeeRepository.findByEmployeeCode(employeeCode)
                    .map(e -> e.getId())
                    .orElse(null);
            busUnitRepository.save(new BusUnit(new CreateBusUnitCommand(plateNumber, route, lat, lng, employeeId)));
        }
    }
}