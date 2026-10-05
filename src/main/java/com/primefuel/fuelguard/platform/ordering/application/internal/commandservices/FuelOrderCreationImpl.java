package com.primefuel.fuelguard.platform.ordering.application.internal.commandservices;

import com.primefuel.fuelguard.platform.ordering.api.FuelOrderCreation;
import com.primefuel.fuelguard.platform.ordering.application.commandservices.FuelOrderCommandService;
import com.primefuel.fuelguard.platform.ordering.domain.model.commands.CreateFuelOrderCommand;
import com.primefuel.fuelguard.platform.shared.application.result.ApplicationError;
import com.primefuel.fuelguard.platform.shared.application.result.Result;
import org.springframework.stereotype.Component;

@Component
public class FuelOrderCreationImpl implements FuelOrderCreation {

    private final FuelOrderCommandService commands;

    public FuelOrderCreationImpl(FuelOrderCommandService commands) {
        this.commands = commands;
    }

    @Override
    public Result<Long, ApplicationError> create(Command command) {
        return commands.handle(new CreateFuelOrderCommand(command.companyId(), command.providerId(),
                        command.fuelProductId(), command.equipmentId(), command.quantity(),
                        command.deliveryAddress(), command.deliveryDate()))
                .map(order -> order.getId());
    }
}
