package com.primefuel.fuelguard.platform.equipment.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateSiteResource(
        @NotBlank @Size(max = 150) String name,
        String address) {
}
