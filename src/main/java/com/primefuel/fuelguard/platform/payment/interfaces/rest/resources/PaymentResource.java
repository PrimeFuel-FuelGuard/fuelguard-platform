package com.primefuel.fuelguard.platform.payment.interfaces.rest.resources;

import com.primefuel.fuelguard.platform.payment.domain.model.valueobjects.PaymentMethod;
import com.primefuel.fuelguard.platform.payment.domain.model.valueobjects.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentResource(Long id, Long orderId, Long companyId, Double amount,
                              PaymentStatus status, PaymentMethod paymentMethod,
                              String transactionReference, LocalDateTime paidAt) {
}
