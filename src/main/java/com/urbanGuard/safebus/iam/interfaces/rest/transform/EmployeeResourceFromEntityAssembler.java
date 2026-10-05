package com.urbanGuard.safebus.iam.interfaces.rest.transform;
import com.urbanGuard.safebus.iam.domain.model.aggregates.Employee;
import com.urbanGuard.safebus.iam.interfaces.rest.resources.EmployeeResource;
public class EmployeeResourceFromEntityAssembler {
    public static EmployeeResource toResourceFromEntity(Employee entity) {
        return new EmployeeResource(entity.getId(), entity.getEmployeeCode(), entity.getFullName(), entity.getEmail(), entity.getRole(), entity.getDni(), entity.isActive(), entity.isDeleted());
    }
}