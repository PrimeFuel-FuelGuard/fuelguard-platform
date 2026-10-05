package com.primefuel.fuelguard.platform.equipment.api;

import java.util.Optional;

public interface CustomerDirectory {

    boolean ownsCustomer(Long organizationId, Long customerAccountId);

    Optional<Long> legacyCompanyIdForCustomer(Long customerAccountId);
}
