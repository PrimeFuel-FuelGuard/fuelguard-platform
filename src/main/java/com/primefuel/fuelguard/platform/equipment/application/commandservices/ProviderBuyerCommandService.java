package com.primefuel.fuelguard.platform.equipment.application.commandservices;

import com.primefuel.fuelguard.platform.equipment.domain.model.commands.RegisterProviderBuyerCommand;
import com.primefuel.fuelguard.platform.shared.application.result.*;

public interface ProviderBuyerCommandService {
    Result<Long, ApplicationError> handle(RegisterProviderBuyerCommand command);
}
