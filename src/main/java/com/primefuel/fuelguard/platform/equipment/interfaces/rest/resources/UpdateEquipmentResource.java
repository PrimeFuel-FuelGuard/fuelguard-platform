package com.primefuel.fuelguard.platform.equipment.interfaces.rest.resources;

import com.primefuel.fuelguard.platform.equipment.domain.model.valueobjects.EquipmentType;
import com.primefuel.fuelguard.platform.inventory.domain.model.valueobjects.FuelType;

public record UpdateEquipmentResource(String name, EquipmentType equipmentType, String licensePlate,
                                      FuelType fuelType, Double tankCapacity, Double currentLevel, String location, String status, Boolean autoRefill,
                                      Integer refillThreshold, String lastRefillDate, Long favoriteProviderId) {
}
