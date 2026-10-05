package com.primefuel.fuelguard.platform.equipment.devicebinding.domain.model.commands;

import java.time.Instant;

public record MoveDeviceCommand(Long bindingId, Long newTankId, Instant at) {
}
