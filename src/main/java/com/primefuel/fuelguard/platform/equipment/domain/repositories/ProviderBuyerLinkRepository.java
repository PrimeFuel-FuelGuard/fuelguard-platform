package com.primefuel.fuelguard.platform.equipment.domain.repositories;

import com.primefuel.fuelguard.platform.equipment.domain.model.aggregates.ProviderBuyerLink;

import java.util.List;
import java.util.Optional;

public interface ProviderBuyerLinkRepository {
    List<ProviderBuyerLink> findByProviderId(Long providerId);

    Optional<ProviderBuyerLink> findByProviderIdAndBuyerCompanyId(
            Long providerId, Long buyerCompanyId);

    ProviderBuyerLink save(ProviderBuyerLink link);
}
