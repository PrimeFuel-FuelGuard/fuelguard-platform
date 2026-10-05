package com.primefuel.fuelguard.platform.iam.application.internal.commandservices;

import com.primefuel.fuelguard.platform.iam.application.commandservices.RoleCommandService;
import com.primefuel.fuelguard.platform.iam.domain.model.commands.SeedRolesCommand;
import com.primefuel.fuelguard.platform.iam.domain.model.entities.Role;
import com.primefuel.fuelguard.platform.iam.domain.model.valueobjects.Roles;
import com.primefuel.fuelguard.platform.iam.domain.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class RoleCommandServiceImpl implements RoleCommandService {

    private final RoleRepository roleRepository;

    public RoleCommandServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void handle(SeedRolesCommand command) {
        Arrays.stream(Roles.values()).forEach(role -> {
            if (!roleRepository.existsByName(role)) {
                roleRepository.save(new Role(Roles.valueOf(role.name())));
            }
        });
    }
}
