package com.primefuel.fuelguard.platform.equipment.domain.model.commands;

public record RegisterSiteCommand(
        Long organizationId,
        Long customerAccountId,
        String name,
        String address) {
}
