package com.primefuel.fuelguard.platform.inventory.domain.model.commands;

public record UpdateFuelProductStockCommand(Long fuelProductId, Double newStock) {
}

