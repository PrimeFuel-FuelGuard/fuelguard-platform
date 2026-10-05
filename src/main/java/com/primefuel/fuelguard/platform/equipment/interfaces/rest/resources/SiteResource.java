package com.primefuel.fuelguard.platform.equipment.interfaces.rest.resources;

public record SiteResource(
        Long id,
        Long organizationId,
        Long customerAccountId,
        String name,
        String address,
        boolean active) {
}
