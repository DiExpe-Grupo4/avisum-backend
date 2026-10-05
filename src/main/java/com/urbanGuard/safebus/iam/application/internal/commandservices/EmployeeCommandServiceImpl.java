package com.urbanGuard.safebus.iam.application.internal.commandservices;

import com.urbanGuard.safebus.iam.application.commandservices.EmployeeCommandService;
import com.urbanGuard.safebus.iam.domain.model.aggregates.Employee;
import com.urbanGuard.safebus.iam.domain.model.commands.*;
import com.urbanGuard.safebus.iam.infrastructure.persistence.jpa.EmployeeRepository;
import com.urbanGuard.safebus.shared.application.result.Result;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class EmployeeCommandServiceImpl implements EmployeeCommandService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeCommandServiceImpl(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Result<Employee, String> handle(CreateEmployeeCommand command) {
        if (command.dni() == null || !command.dni().matches("\\d{8}")) {
            return Result.err("El DNI debe tener exactamente 8 dígitos");
        }
        if (employeeRepository.existsByEmailAndDeletedFalse(command.email())) {
            return Result.err("Email already in use: " + command.email());
        }
        if (employeeRepository.existsByDniAndDeletedFalse(command.dni())) {
            return Result.err("DNI already in use: " + command.dni());
        }

        String code = (command.employeeCode() == null || command.employeeCode().isBlank())
                ? generateNextEmployeeCode()
                : command.employeeCode();

        if (employeeRepository.existsByEmployeeCode(code)) {
            return Result.err("Employee code already exists: " + code);
        }

        var hashedCommand = new CreateEmployeeCommand(
                code,
                command.fullName(),
                command.email(),
                passwordEncoder.encode(command.password()),
                command.role(),
                command.dni()
        );
        var employee = new Employee(hashedCommand);
        employeeRepository.save(employee);
        return Result.ok(employee);
    }

    @Override
    public Result<Employee, String> handle(LoginCommand command) {
        var employeeOpt = employeeRepository.findByEmployeeCode(command.employeeCode());
        if (employeeOpt.isEmpty()) {
            return Result.err("Credenciales inválidas");
        }
        var employee = employeeOpt.get();

        if (!employee.canLogIn()) {
            return Result.err("Conductor desactivado. Contacta al administrador.");
        }

        if (employee.isLocked()) {
            return Result.err("Cuenta bloqueada temporalmente por múltiples intentos fallidos. Intenta más tarde.");
        }

        if (!passwordEncoder.matches(command.password(), employee.getPassword())) {
            employee.registerFailedAttempt();
            employeeRepository.save(employee);
            return Result.err("Credenciales inválidas");
        }

        employee.resetFailedAttempts();
        employeeRepository.save(employee);
        return Result.ok(employee);
    }

    @Override
    public Result<Employee, String> handle(UpdateEmployeeCommand command) {
        var employeeOpt = employeeRepository.findById(command.employeeId());
        if (employeeOpt.isEmpty()) {
            return Result.err("Empleado no encontrado: " + command.employeeId());
        }
        if (command.dni() == null || !command.dni().matches("\\d{8}")) {
            return Result.err("El DNI debe tener exactamente 8 dígitos");
        }
        if (employeeRepository.existsByEmailAndDeletedFalseAndIdNot(command.email(), command.employeeId())) {
            return Result.err("Email already in use: " + command.email());
        }
        if (employeeRepository.existsByDniAndDeletedFalseAndIdNot(command.dni(), command.employeeId())) {
            return Result.err("DNI already in use: " + command.dni());
        }

        var employee = employeeOpt.get();
        employee.updateProfile(command.fullName(), command.email(), command.dni(), command.role());
        employeeRepository.save(employee);
        return Result.ok(employee);
    }

    @Override
    public Result<Employee, String> handle(DeactivateEmployeeCommand command) {
        var employeeOpt = employeeRepository.findById(command.employeeId());
        if (employeeOpt.isEmpty()) {
            return Result.err("Empleado no encontrado: " + command.employeeId());
        }
        var employee = employeeOpt.get();
        employee.deactivate();
        employeeRepository.save(employee);
        return Result.ok(employee);
    }

    @Override
    public Result<Employee, String> handle(ReactivateEmployeeCommand command) {
        var employeeOpt = employeeRepository.findById(command.employeeId());
        if (employeeOpt.isEmpty()) {
            return Result.err("Empleado no encontrado: " + command.employeeId());
        }
        var employee = employeeOpt.get();
        employee.reactivate();
        employeeRepository.save(employee);
        return Result.ok(employee);
    }

    @Override
    public Result<Employee, String> handle(DeleteEmployeeCommand command) {
        var employeeOpt = employeeRepository.findById(command.employeeId());
        if (employeeOpt.isEmpty()) {
            return Result.err("Empleado no encontrado: " + command.employeeId());
        }
        var employee = employeeOpt.get();
        employee.softDelete();
        employeeRepository.save(employee);
        return Result.ok(employee);
    }

    /** Busca el máximo código EMP-XXX existente (incluyendo eliminados) y devuelve el siguiente. */
    private String generateNextEmployeeCode() {
        int max = employeeRepository.findAll().stream()
                .map(Employee::getEmployeeCode)
                .filter(code -> code != null && code.startsWith("EMP-"))
                .map(code -> code.substring(4))
                .filter(suffix -> suffix.matches("\\d+"))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);
        return String.format("EMP-%03d", max + 1);
    }
}