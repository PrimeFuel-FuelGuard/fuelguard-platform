package com.primefuel.fuelguard.platform.fulfillment;

import com.primefuel.fuelguard.platform.fulfillment.domain.repositories.DeliveryStateTransitionRepository;
import com.primefuel.fuelguard.platform.fulfillment.infrastructure.persistence.jpa.repositories.DeliveryBusinessJournalRepository;
import com.primefuel.fuelguard.platform.safety.domain.repositories.SafetyDecisionRepository;
import com.primefuel.fuelguard.platform.safety.valve.infrastructure.persistence.SafetyIncidentRepository;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class DeliveryJournalImmutabilityTest {
    @Test void journalSourcesExposeNoUpdateOrDeleteMethods() {
        var repositories = List.of(DeliveryStateTransitionRepository.class, SafetyDecisionRepository.class,
                SafetyIncidentRepository.class, DeliveryBusinessJournalRepository.class);
        for (var repository : repositories) {
            assertThat(java.util.Arrays.stream(repository.getMethods()).map(java.lang.reflect.Method::getName)
                    .filter(name -> name.toLowerCase().contains("update") || name.toLowerCase().contains("delete")))
                    .as(repository.getSimpleName()).isEmpty();
        }
    }
}
