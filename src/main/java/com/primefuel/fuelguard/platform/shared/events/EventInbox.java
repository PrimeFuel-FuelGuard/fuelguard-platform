package com.primefuel.fuelguard.platform.shared.events;

public interface EventInbox {

    boolean consume(String consumer, String eventId);
}
