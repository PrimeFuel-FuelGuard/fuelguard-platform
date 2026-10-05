package com.primefuel.fuelguard.platform.supply.domain.model.commands;

public record ReserveSupplyCommand(
        Long providerId,
        Long fuelProductId,
        String reference,
        Double quantity,
        String unit) {
}
