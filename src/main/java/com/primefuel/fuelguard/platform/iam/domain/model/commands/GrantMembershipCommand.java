package com.primefuel.fuelguard.platform.iam.domain.model.commands;

import com.primefuel.fuelguard.platform.iam.domain.model.valueobjects.MembershipRole;

public record GrantMembershipCommand(
        Long organizationId,
        Long userId,
        MembershipRole role) {
}
