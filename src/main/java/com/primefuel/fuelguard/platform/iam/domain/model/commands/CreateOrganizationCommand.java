package com.primefuel.fuelguard.platform.iam.domain.model.commands;

import com.primefuel.fuelguard.platform.iam.domain.model.valueobjects.OrganizationType;

public record CreateOrganizationCommand(
        String name,
        String ruc,
        OrganizationType type) {
}
