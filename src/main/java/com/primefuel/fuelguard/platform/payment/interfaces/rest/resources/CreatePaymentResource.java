package com.primefuel.fuelguard.platform.payment.interfaces.rest.resources;

import com.primefuel.fuelguard.platform.payment.domain.model.valueobjects.PaymentMethod;

public record CreatePaymentResource(Long orderId, Long companyId, Double amount, PaymentMethod paymentMethod) {
}
