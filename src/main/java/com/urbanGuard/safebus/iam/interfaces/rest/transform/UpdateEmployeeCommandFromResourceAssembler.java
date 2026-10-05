package com.urbanGuard.safebus.iam.interfaces.rest.transform;
import com.urbanGuard.safebus.iam.domain.model.commands.UpdateEmployeeCommand;
import com.urbanGuard.safebus.iam.interfaces.rest.resources.UpdateEmployeeResource;
public class UpdateEmployeeCommandFromResourceAssembler {
    public static UpdateEmployeeCommand toCommandFromResource(Long employeeId, UpdateEmployeeResource resource) {
        return new UpdateEmployeeCommand(employeeId, resource.fullName(), resource.email(), resource.dni(), resource.role());
    }
}