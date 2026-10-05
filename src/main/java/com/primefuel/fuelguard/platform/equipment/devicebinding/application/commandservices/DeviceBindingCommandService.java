package com.primefuel.fuelguard.platform.equipment.devicebinding.application.commandservices;

import com.primefuel.fuelguard.platform.equipment.devicebinding.domain.model.aggregates.DeviceBinding;
import com.primefuel.fuelguard.platform.equipment.devicebinding.domain.model.commands.BindDeviceCommand;
import com.primefuel.fuelguard.platform.equipment.devicebinding.domain.model.commands.MoveDeviceCommand;
import com.primefuel.fuelguard.platform.equipment.devicebinding.domain.model.commands.RevokeDeviceCommand;
import com.primefuel.fuelguard.platform.shared.application.result.ApplicationError;
import com.primefuel.fuelguard.platform.shared.application.result.Result;

public interface DeviceBindingCommandService {
    Result<DeviceBinding, ApplicationError> handle(BindDeviceCommand command);
    Result<DeviceBinding, ApplicationError> handle(RevokeDeviceCommand command);
    Result<DeviceBinding, ApplicationError> handle(MoveDeviceCommand command);
}
