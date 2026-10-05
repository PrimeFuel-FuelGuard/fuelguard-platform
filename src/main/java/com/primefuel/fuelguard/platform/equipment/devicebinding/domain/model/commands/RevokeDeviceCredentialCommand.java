package com.primefuel.fuelguard.platform.equipment.devicebinding.domain.model.commands;

public record RevokeDeviceCredentialCommand(String deviceId, String channel) {
}
