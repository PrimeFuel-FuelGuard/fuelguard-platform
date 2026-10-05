package com.primefuel.fuelguard.platform.iam.application.commandservices;

import com.primefuel.fuelguard.platform.iam.domain.model.aggregates.BuyerCompany;
import com.primefuel.fuelguard.platform.iam.domain.model.commands.CreateBuyerCompanyCommand;
import com.primefuel.fuelguard.platform.shared.application.result.ApplicationError;
import com.primefuel.fuelguard.platform.shared.application.result.Result;

public interface BuyerCompanyCommandService {
    Result<BuyerCompany, ApplicationError> handle(CreateBuyerCompanyCommand command);
}
