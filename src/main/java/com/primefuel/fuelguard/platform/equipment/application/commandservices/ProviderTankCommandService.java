package com.primefuel.fuelguard.platform.equipment.application.commandservices;

import com.primefuel.fuelguard.platform.equipment.domain.model.commands.*;
import com.primefuel.fuelguard.platform.shared.application.result.*;

public interface ProviderTankCommandService {
    Result<Long, ApplicationError> handle(RegisterProviderTankCommand command);

    Result<Long, ApplicationError> handle(UpdateProviderTankCommand command);
}
