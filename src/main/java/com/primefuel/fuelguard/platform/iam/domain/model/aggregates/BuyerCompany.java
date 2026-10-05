package com.primefuel.fuelguard.platform.iam.domain.model.aggregates;

import com.primefuel.fuelguard.platform.iam.domain.model.commands.CreateBuyerCompanyCommand;
import com.primefuel.fuelguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BuyerCompany extends AbstractDomainAggregateRoot<BuyerCompany> {

    private Long id;
    private String name;
    private String ruc;
    private String sector;
    private String address;
    private String contactEmail;
    private String phone;

    public BuyerCompany(CreateBuyerCompanyCommand command) {
        this.name = command.name();
        this.ruc = command.ruc();
        this.sector = command.sector();
        this.address = command.address();
        this.contactEmail = command.contactEmail();
        this.phone = command.phone();
    }
}
