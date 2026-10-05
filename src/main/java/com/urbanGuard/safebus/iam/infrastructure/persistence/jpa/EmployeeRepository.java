package com.urbanGuard.safebus.iam.infrastructure.persistence.jpa;

import com.urbanGuard.safebus.iam.domain.model.aggregates.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmployeeCode(String employeeCode);
    boolean existsByEmployeeCode(String employeeCode);
    boolean existsByEmail(String email);
    boolean existsByDni(String dni);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByDniAndIdNot(String dni, Long id);

    // Unicidad real: solo cuenta contra conductores NO eliminados (soft delete libera el dato)
    boolean existsByEmailAndDeletedFalse(String email);
    boolean existsByDniAndDeletedFalse(String dni);
    boolean existsByEmailAndDeletedFalseAndIdNot(String email, Long id);
    boolean existsByDniAndDeletedFalseAndIdNot(String dni, Long id);
}