package com.primefuel.fuelguard.platform.payment.application.queryservices;

import com.primefuel.fuelguard.platform.payment.domain.model.aggregates.Payment;
import com.primefuel.fuelguard.platform.payment.domain.model.queries.GetAllPaymentsQuery;
import com.primefuel.fuelguard.platform.payment.domain.model.queries.GetPaymentsByProviderIdQuery;
import com.primefuel.fuelguard.platform.payment.domain.model.valueobjects.ProviderPayment;
import com.primefuel.fuelguard.platform.payment.domain.model.queries.GetPaymentByIdQuery;
import com.primefuel.fuelguard.platform.payment.domain.model.queries.GetPaymentByOrderIdQuery;
import com.primefuel.fuelguard.platform.payment.domain.model.queries.GetPaymentsByCompanyIdQuery;

import java.util.List;
import java.util.Optional;

public interface PaymentQueryService {
    List<ProviderPayment> handle(GetPaymentsByProviderIdQuery query);

    Optional<Payment> handle(GetPaymentByIdQuery query);
    Optional<Payment> handle(GetPaymentByOrderIdQuery query);
    List<Payment> handle(GetAllPaymentsQuery query);
    List<Payment> handle(GetPaymentsByCompanyIdQuery query);
}
