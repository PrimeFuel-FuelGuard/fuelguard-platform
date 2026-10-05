package com.primefuel.fuelguard.platform.replenishment.application.queryservices;

import com.primefuel.fuelguard.platform.replenishment.domain.model.aggregates.ReplenishmentRequest;
import com.primefuel.fuelguard.platform.replenishment.domain.model.queries.GetReplenishmentRequestByIdQuery;
import com.primefuel.fuelguard.platform.replenishment.domain.model.queries.GetReplenishmentRequestsByOrganizationQuery;
import com.primefuel.fuelguard.platform.replenishment.domain.model.queries.GetReplenishmentRequestsByProviderQuery;

import java.util.List;
import java.util.Optional;

public interface ReplenishmentQueryService {
    Optional<ReplenishmentRequest> handle(GetReplenishmentRequestByIdQuery query);
    List<ReplenishmentRequest> handle(GetReplenishmentRequestsByOrganizationQuery query);
    List<ReplenishmentRequest> handle(GetReplenishmentRequestsByProviderQuery query);
}
