package com.primefuel.fuelguard.platform.iam.application.internal.commandservices;

import com.primefuel.fuelguard.platform.iam.application.commandservices.OrganizationCommandService;
import com.primefuel.fuelguard.platform.iam.domain.model.aggregates.Organization;
import com.primefuel.fuelguard.platform.iam.domain.model.commands.CreateOrganizationCommand;
import com.primefuel.fuelguard.platform.iam.domain.repositories.OrganizationRepository;
import com.primefuel.fuelguard.platform.shared.application.result.ApplicationError;
import com.primefuel.fuelguard.platform.shared.application.result.Result;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class OrganizationCommandServiceImpl implements OrganizationCommandService {

    private final OrganizationRepository organizationRepository;

    public OrganizationCommandServiceImpl(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Override
    public Result<Organization, ApplicationError> handle(CreateOrganizationCommand command) {
        if (organizationRepository.findByRuc(command.ruc()).isPresent()) {
            return Result.failure(ApplicationError.conflict("Organization", "An organization with that RUC already exists"));
        }
        var organization = new Organization(command);
        try {
            return Result.success(organizationRepository.save(organization));
        } catch (DataIntegrityViolationException exception) {
            return Result.failure(ApplicationError.conflict("Organization", "An organization with that RUC already exists"));
        }
    }
}
