package com.primefuel.fuelguard.platform.telemetry.interfaces.rest.resources;

import java.time.Instant;

public record ProviderTankReadingResource(
        Long id,
        Long tankId,
        String deviceId,
        String channel,
        long sequence,
        double level,
        String unit,
        Instant capturedAt,
        Instant receivedAt,
        String quality) {}
