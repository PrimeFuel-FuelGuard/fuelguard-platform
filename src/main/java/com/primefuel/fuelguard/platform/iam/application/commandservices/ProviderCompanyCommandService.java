package com.primefuel.fuelguard.platform.iam.application.commandservices;

import com.primefuel.fuelguard.platform.iam.domain.model.aggregates.ProviderCompany;
import com.primefuel.fuelguard.platform.iam.domain.model.commands.CreateProviderCompanyCommand;
import com.primefuel.fuelguard.platform.shared.application.result.ApplicationError;
import com.primefuel.fuelguard.platform.shared.application.result.Result;

public interface ProviderCompanyCommandService {
    Result<ProviderCompany, ApplicationError> handle(CreateProviderCompanyCommand command);
}
