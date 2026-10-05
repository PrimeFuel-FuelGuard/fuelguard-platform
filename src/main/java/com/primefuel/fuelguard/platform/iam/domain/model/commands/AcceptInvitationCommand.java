package com.primefuel.fuelguard.platform.iam.domain.model.commands;

public record AcceptInvitationCommand(
        String token,
        Long userId) {
}
