package com.primefuel.fuelguard.platform.fulfillment.domain.model.commands;

/** Explicitly materializes the {@code ASSIGNED} state and its event (assignment orchestration is T15-A). */
public record AssignDeliveryCommand(Long deliveryId) {
}
