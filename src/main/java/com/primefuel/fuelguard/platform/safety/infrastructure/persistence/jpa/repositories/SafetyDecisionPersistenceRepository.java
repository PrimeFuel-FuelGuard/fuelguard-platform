package com.primefuel.fuelguard.platform.safety.infrastructure.persistence.jpa.repositories;

import com.primefuel.fuelguard.platform.safety.infrastructure.persistence.jpa.entities.SafetyDecisionPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SafetyDecisionPersistenceRepository
        extends JpaRepository<SafetyDecisionPersistenceEntity, Long> {

    List<SafetyDecisionPersistenceEntity> findByDeliveryIdOrderByEvaluatedAtAsc(Long deliveryId);

    long countByDeliveryId(Long deliveryId);
}
