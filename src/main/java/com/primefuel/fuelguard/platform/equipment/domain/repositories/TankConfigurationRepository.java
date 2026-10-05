package com.primefuel.fuelguard.platform.equipment.domain.repositories;

import com.primefuel.fuelguard.platform.equipment.domain.model.aggregates.TankConfiguration;

import java.util.List;

public interface TankConfigurationRepository {
    List<TankConfiguration> findByTankId(Long tankId);
    TankConfiguration save(TankConfiguration configuration);
}
