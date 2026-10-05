// ShiftCommandService.java
package com.urbanGuard.safebus.monitoring.application.commandservices;

import com.urbanGuard.safebus.monitoring.domain.model.aggregates.Shift;
import com.urbanGuard.safebus.monitoring.domain.model.commands.EndShiftCommand;
import com.urbanGuard.safebus.monitoring.domain.model.commands.StartShiftCommand;
import com.urbanGuard.safebus.monitoring.domain.model.commands.UpdateShiftProgressCommand;
import com.urbanGuard.safebus.shared.application.result.Result;

public interface ShiftCommandService {
    Result<Shift, String> handle(StartShiftCommand command);
    Result<Shift, String> handle(EndShiftCommand command);
    Result<Shift, String> handle(UpdateShiftProgressCommand command);
}