package com.primefuel.fuelguard.platform.equipment.domain.model.commands;

public record UpdateTankConfigurationCommand(
        Long tankId,
        String fuelType,
        Double capacity,
        String unit) {
}
