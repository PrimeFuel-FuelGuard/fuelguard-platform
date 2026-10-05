package com.primefuel.fuelguard.platform.iam.application.commandservices;

import com.primefuel.fuelguard.platform.iam.domain.model.aggregates.Organization;
import com.primefuel.fuelguard.platform.iam.domain.model.commands.OnboardOrganizationCommand;
import com.primefuel.fuelguard.platform.shared.application.result.ApplicationError;
import com.primefuel.fuelguard.platform.shared.application.result.Result;

public interface OnboardingCommandService {
    Result<Organization, ApplicationError> handle(OnboardOrganizationCommand command);
}
