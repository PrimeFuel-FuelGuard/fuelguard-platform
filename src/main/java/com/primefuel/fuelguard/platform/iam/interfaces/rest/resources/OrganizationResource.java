package com.primefuel.fuelguard.platform.iam.interfaces.rest.resources;

public record OrganizationResource(
        Long id,
        String name,
        String type,
        String role) {
}
