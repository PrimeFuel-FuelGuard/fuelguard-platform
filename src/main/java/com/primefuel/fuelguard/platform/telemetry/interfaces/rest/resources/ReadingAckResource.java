package com.primefuel.fuelguard.platform.telemetry.interfaces.rest.resources;

public record ReadingAckResource(
        Long readingId,
        String quality,
        Long tankId,
        boolean duplicate) {
}
