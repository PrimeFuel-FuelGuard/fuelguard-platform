package com.primefuel.fuelguard.platform.equipment.domain.model.commands;

public record UpdateProviderTankCommand(
        Long providerId,
        Long tankId,
        Long fuelProductId,
        Double lowLevelPercent,
        String deviceId,
        String channel,
        Boolean autoGenerateEnabled) {}
