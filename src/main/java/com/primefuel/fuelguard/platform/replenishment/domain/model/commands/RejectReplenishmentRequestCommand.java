package com.primefuel.fuelguard.platform.replenishment.domain.model.commands;

public record RejectReplenishmentRequestCommand(Long requestId, String reason) {
}
