package com.primefuel.fuelguard.platform.equipment.application.queryservices;

import com.primefuel.fuelguard.platform.equipment.domain.model.queries.GetProviderTanksQuery;
import com.primefuel.fuelguard.platform.equipment.domain.model.queries.GetProviderTankByIdQuery;
import com.primefuel.fuelguard.platform.equipment.domain.model.valueobjects.ProviderTank;
import com.primefuel.fuelguard.platform.shared.application.result.ApplicationError;
import com.primefuel.fuelguard.platform.shared.application.result.Result;

import java.util.List;

public interface ProviderTankQueryService {
    Result<ProviderTank, ApplicationError> handle(GetProviderTankByIdQuery query);

    Result<List<ProviderTank>, ApplicationError> handle(GetProviderTanksQuery query);
}
