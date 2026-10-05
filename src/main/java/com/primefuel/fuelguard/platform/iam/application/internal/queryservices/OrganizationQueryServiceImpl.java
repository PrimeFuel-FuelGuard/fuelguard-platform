package com.primefuel.fuelguard.platform.iam.application.internal.queryservices;

import com.primefuel.fuelguard.platform.iam.application.queryservices.OrganizationQueryService;
import com.primefuel.fuelguard.platform.iam.domain.model.aggregates.Organization;
import com.primefuel.fuelguard.platform.iam.domain.model.queries.GetOrganizationByIdQuery;
import com.primefuel.fuelguard.platform.iam.domain.repositories.OrganizationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrganizationQueryServiceImpl implements OrganizationQueryService {

    private final OrganizationRepository organizationRepository;

    public OrganizationQueryServiceImpl(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Override
    public Optional<Organization> handle(GetOrganizationByIdQuery query) {
        return organizationRepository.findById(query.organizationId());
    }
}
