package com.primefuel.fuelguard.platform.iam.application.commandservices;

import com.primefuel.fuelguard.platform.iam.domain.model.commands.SeedRolesCommand;

public interface RoleCommandService {
    void handle(SeedRolesCommand command);
}
