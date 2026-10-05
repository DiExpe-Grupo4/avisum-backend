package com.urbanGuard.safebus.iam.interfaces.rest.transform;
import com.urbanGuard.safebus.iam.domain.model.commands.CreateEmployeeCommand;
import com.urbanGuard.safebus.iam.interfaces.rest.resources.CreateEmployeeResource;
public class CreateEmployeeCommandFromResourceAssembler {
    public static CreateEmployeeCommand toCommandFromResource(CreateEmployeeResource resource) {
        return new CreateEmployeeCommand(resource.employeeCode(), resource.fullName(), resource.email(), resource.password(), resource.role(), resource.dni());
    }
}