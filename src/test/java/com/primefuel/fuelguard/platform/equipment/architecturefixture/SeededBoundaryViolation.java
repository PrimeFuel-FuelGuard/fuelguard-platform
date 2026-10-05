package com.primefuel.fuelguard.platform.equipment.architecturefixture;

import com.primefuel.fuelguard.platform.ordering.domain.repositories.FuelOrderRepository;

public class SeededBoundaryViolation {

    private FuelOrderRepository leakedDependency;

    public FuelOrderRepository leakedDependency() {
        return leakedDependency;
    }
}
