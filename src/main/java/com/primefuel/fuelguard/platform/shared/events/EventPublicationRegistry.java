package com.primefuel.fuelguard.platform.shared.events;

public interface EventPublicationRegistry {

    EventEnvelope publish(String eventType,
                          String aggregateType,
                          String aggregateId,
                          Long organizationId,
                          Long aggregateVersion,
                          String payloadJson);
}
