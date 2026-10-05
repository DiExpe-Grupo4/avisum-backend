package com.urbanGuard.safebus.iam.application.commandservices;

import com.urbanGuard.safebus.iam.domain.model.aggregates.Employee;
import com.urbanGuard.safebus.iam.domain.model.commands.*;
import com.urbanGuard.safebus.shared.application.result.Result;

public interface EmployeeCommandService {
    Result<Employee, String> handle(CreateEmployeeCommand command);
    Result<Employee, String> handle(LoginCommand command);
    Result<Employee, String> handle(UpdateEmployeeCommand command);
    Result<Employee, String> handle(DeactivateEmployeeCommand command);
    Result<Employee, String> handle(ReactivateEmployeeCommand command);
    Result<Employee, String> handle(DeleteEmployeeCommand command);
}