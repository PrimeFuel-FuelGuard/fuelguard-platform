package com.primefuel.fuelguard.platform.iam.domain.model.commands;

import com.primefuel.fuelguard.platform.iam.domain.model.valueobjects.MembershipRole;

public record InviteMemberCommand(
        Long organizationId,
        String email,
        MembershipRole role,
        Long invitedByUserId) {
}
