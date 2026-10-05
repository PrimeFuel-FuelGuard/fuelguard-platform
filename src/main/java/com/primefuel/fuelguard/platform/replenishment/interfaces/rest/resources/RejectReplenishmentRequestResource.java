package com.primefuel.fuelguard.platform.replenishment.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record RejectReplenishmentRequestResource(@NotBlank String reason) {
}
