package com.primefuel.fuelguard.platform.fulfillment.domain.model.commands;

/** Explicit cancellation of a delivery that will not be fulfilled; it is a terminal physical state. */
public record CancelDeliveryCommand(Long deliveryId, String reason) {
}
