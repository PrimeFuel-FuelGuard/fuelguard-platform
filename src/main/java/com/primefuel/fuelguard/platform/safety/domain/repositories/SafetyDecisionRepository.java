package com.primefuel.fuelguard.platform.safety.domain.repositories;

import com.primefuel.fuelguard.platform.safety.domain.model.entities.SafetyDecision;

import java.util.List;

/**
 * Persistence port of safety decisions (S17). Append-only: decisions are written once, never updated or
 * deleted.
 */
public interface SafetyDecisionRepository {

    SafetyDecision save(SafetyDecision decision);

    List<SafetyDecision> findByDeliveryId(Long deliveryId);

    long countByDeliveryId(Long deliveryId);
}
