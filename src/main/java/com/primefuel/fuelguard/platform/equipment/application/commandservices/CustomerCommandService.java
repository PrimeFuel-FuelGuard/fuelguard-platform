package com.primefuel.fuelguard.platform.equipment.application.commandservices;

import com.primefuel.fuelguard.platform.equipment.domain.model.aggregates.CustomerAccount;
import com.primefuel.fuelguard.platform.equipment.domain.model.aggregates.CustomerSite;
import com.primefuel.fuelguard.platform.equipment.domain.model.commands.RegisterCustomerCommand;
import com.primefuel.fuelguard.platform.equipment.domain.model.commands.RegisterSiteCommand;
import com.primefuel.fuelguard.platform.shared.application.result.ApplicationError;
import com.primefuel.fuelguard.platform.shared.application.result.Result;

public interface CustomerCommandService {
    Result<CustomerAccount, ApplicationError> handle(RegisterCustomerCommand command);
    Result<CustomerSite, ApplicationError> handle(RegisterSiteCommand command);
}
