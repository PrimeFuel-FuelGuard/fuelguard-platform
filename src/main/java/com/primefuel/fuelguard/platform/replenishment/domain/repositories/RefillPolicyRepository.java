package com.primefuel.fuelguard.platform.replenishment.domain.repositories;

import com.primefuel.fuelguard.platform.replenishment.domain.model.aggregates.RefillPolicy;

import java.util.Optional;

public interface RefillPolicyRepository {
    Optional<RefillPolicy> findByTankId(Long tankId);
    RefillPolicy save(RefillPolicy policy);
}
