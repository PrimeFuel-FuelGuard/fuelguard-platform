package com.primefuel.fuelguard.platform.iam.application.commandservices;

import com.primefuel.fuelguard.platform.iam.domain.model.aggregates.Organization;
import com.primefuel.fuelguard.platform.iam.domain.model.commands.CreateOrganizationCommand;
import com.primefuel.fuelguard.platform.shared.application.result.ApplicationError;
import com.primefuel.fuelguard.platform.shared.application.result.Result;

public interface OrganizationCommandService {
    Result<Organization, ApplicationError> handle(CreateOrganizationCommand command);
}
