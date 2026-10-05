package com.primefuel.fuelguard.platform.replenishment.interfaces.rest.transform;

import com.primefuel.fuelguard.platform.replenishment.domain.model.aggregates.RefillPolicy;
import com.primefuel.fuelguard.platform.replenishment.interfaces.rest.resources.RefillPolicyResource;

public final class RefillPolicyResourceFromDomainAssembler {

    private RefillPolicyResourceFromDomainAssembler() {
    }

    public static RefillPolicyResource toResourceFromDomain(RefillPolicy policy) {
        return new RefillPolicyResource(
                policy.getId(),
                policy.getTankId(),
                policy.getOrganizationId(),
                policy.getLowLevelPercent(),
                policy.getHysteresisPercent(),
                policy.getTargetLevelPercent(),
                policy.getProviderId(),
                policy.getFuelProductId(),
                policy.isAutoGenerateEnabled(),
                policy.getPolicyVersion(),
                policy.getVersion());
    }
}
