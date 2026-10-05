package com.primefuel.fuelguard.platform.shared.infrastructure.events;

import com.primefuel.fuelguard.platform.shared.events.EventInbox;
import com.primefuel.fuelguard.platform.shared.infrastructure.persistence.jpa.entities.ConsumedEventPersistenceEntity;
import com.primefuel.fuelguard.platform.shared.infrastructure.persistence.jpa.repositories.ConsumedEventPersistenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JpaEventInbox implements EventInbox {

    private final ConsumedEventPersistenceRepository repository;

    public JpaEventInbox(ConsumedEventPersistenceRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public boolean consume(String consumer, String eventId) {
        if (repository.existsByConsumerAndEventId(consumer, eventId)) {
            return false;
        }
        var consumed = new ConsumedEventPersistenceEntity();
        consumed.setConsumer(consumer);
        consumed.setEventId(eventId);
        repository.save(consumed);
        return true;
    }
}
