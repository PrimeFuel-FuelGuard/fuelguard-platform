package com.primefuel.fuelguard.platform.equipment.interfaces.rest.resources;

public record CustomerResource(
        Long id,
        Long organizationId,
        String name,
        String ruc,
        String address,
        String contactEmail,
        String phone,
        Long legacyCompanyId,
        boolean active) {
}
