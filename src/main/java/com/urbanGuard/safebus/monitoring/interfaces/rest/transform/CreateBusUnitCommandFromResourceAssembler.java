package com.urbanGuard.safebus.monitoring.interfaces.rest.transform;
import com.urbanGuard.safebus.monitoring.domain.model.commands.CreateBusUnitCommand;
import com.urbanGuard.safebus.monitoring.interfaces.rest.resources.CreateBusUnitResource;
public class CreateBusUnitCommandFromResourceAssembler {
    public static CreateBusUnitCommand toCommandFromResource(CreateBusUnitResource r) {
        return new CreateBusUnitCommand(r.plateNumber(), r.route(), r.latitude(), r.longitude(), r.assignedEmployeeId());
    }
}