package com.primefuel.fuelguard.platform.equipment.application.commandservices;

import com.primefuel.fuelguard.platform.equipment.domain.model.aggregates.Equipment;
import com.primefuel.fuelguard.platform.equipment.domain.model.commands.CreateEquipmentCommand;
import com.primefuel.fuelguard.platform.equipment.domain.model.commands.UpdateEquipmentCommand;
import com.primefuel.fuelguard.platform.shared.application.result.ApplicationError;
import com.primefuel.fuelguard.platform.shared.application.result.Result;

public interface EquipmentCommandService {
    Result<Equipment, ApplicationError> handle(CreateEquipmentCommand command);
    Result<Equipment, ApplicationError> handle(UpdateEquipmentCommand command);
}
