package com.primefuel.fuelguard.platform.iam.application.internal.queryservices;

import com.primefuel.fuelguard.platform.iam.application.queryservices.MembershipQueryService;
import com.primefuel.fuelguard.platform.iam.domain.model.aggregates.Membership;
import com.primefuel.fuelguard.platform.iam.domain.model.queries.GetMembershipsByUserIdQuery;
import com.primefuel.fuelguard.platform.iam.domain.repositories.MembershipRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MembershipQueryServiceImpl implements MembershipQueryService {

    private final MembershipRepository membershipRepository;

    public MembershipQueryServiceImpl(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    @Override
    public List<Membership> handle(GetMembershipsByUserIdQuery query) {
        return membershipRepository.findByUserId(query.userId());
    }
}
