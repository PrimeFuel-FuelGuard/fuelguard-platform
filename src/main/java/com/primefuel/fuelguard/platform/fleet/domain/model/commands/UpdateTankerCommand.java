package com.primefuel.fuelguard.platform.fleet.domain.model.commands;

/** Updates a tanker's mutable data; like {@link UpdateDriverCommand} it cannot transfer the tenant. */
public record UpdateTankerCommand(
        Long tankerId,
        String licensePlate,
        String brand,
        String model,
        Double capacity,
        String unit,
        String status) {
}
