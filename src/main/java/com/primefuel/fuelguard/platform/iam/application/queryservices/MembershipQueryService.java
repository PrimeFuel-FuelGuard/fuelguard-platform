package com.primefuel.fuelguard.platform.iam.application.queryservices;

import com.primefuel.fuelguard.platform.iam.domain.model.aggregates.Membership;
import com.primefuel.fuelguard.platform.iam.domain.model.queries.GetMembershipsByUserIdQuery;

import java.util.List;

public interface MembershipQueryService {
    List<Membership> handle(GetMembershipsByUserIdQuery query);
}
