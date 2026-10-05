package com.primefuel.fuelguard.platform.iam.application.queryservices;

import com.primefuel.fuelguard.platform.iam.domain.model.aggregates.BuyerCompany;
import com.primefuel.fuelguard.platform.iam.domain.model.queries.GetAllBuyerCompaniesQuery;
import com.primefuel.fuelguard.platform.iam.domain.model.queries.GetBuyerCompanyByIdQuery;

import java.util.List;
import java.util.Optional;

public interface BuyerCompanyQueryService {
    Optional<BuyerCompany> handle(GetBuyerCompanyByIdQuery query);
    List<BuyerCompany> handle(GetAllBuyerCompaniesQuery query);
}
