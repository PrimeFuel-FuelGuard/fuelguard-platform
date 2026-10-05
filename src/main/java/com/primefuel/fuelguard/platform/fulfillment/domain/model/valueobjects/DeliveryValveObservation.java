package com.primefuel.fuelguard.platform.fulfillment.domain.model.valueobjects;

import java.time.Instant;

public record DeliveryValveObservation(
        Long id, String state, boolean unauthorized, String commandId, Instant recordedAt) {}
