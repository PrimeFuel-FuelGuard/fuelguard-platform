package com.primefuel.fuelguard.platform.ordering.application.commandservices;

import com.primefuel.fuelguard.platform.ordering.domain.model.aggregates.FuelOrder;
import com.primefuel.fuelguard.platform.ordering.domain.model.commands.CancelFuelOrderCommand;
import com.primefuel.fuelguard.platform.ordering.domain.model.commands.ConfirmFuelOrderCommand;
import com.primefuel.fuelguard.platform.ordering.domain.model.commands.CreateFuelOrderCommand;
import com.primefuel.fuelguard.platform.shared.application.result.ApplicationError;
import com.primefuel.fuelguard.platform.shared.application.result.Result;

public interface FuelOrderCommandService {
    Result<FuelOrder, ApplicationError> handle(CreateFuelOrderCommand command);
    Result<FuelOrder, ApplicationError> handle(ConfirmFuelOrderCommand command);
    Result<FuelOrder, ApplicationError> handle(CancelFuelOrderCommand command);
}
