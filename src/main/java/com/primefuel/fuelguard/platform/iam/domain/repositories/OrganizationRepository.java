package com.primefuel.fuelguard.platform.iam.domain.repositories;

import com.primefuel.fuelguard.platform.iam.domain.model.aggregates.Organization;

import java.util.Optional;

public interface OrganizationRepository {
    Optional<Organization> findById(Long id);
    Optional<Organization> findByRuc(String ruc);
    Organization save(Organization organization);
    boolean existsById(Long id);
}
