package com.primefuel.fuelguard.platform.fulfillment.domain.model.commands;

public record FailDeliveryCommand(Long deliveryId, String reason) {
}
