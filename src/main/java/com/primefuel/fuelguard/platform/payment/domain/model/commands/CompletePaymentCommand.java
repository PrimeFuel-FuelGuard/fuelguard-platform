package com.primefuel.fuelguard.platform.payment.domain.model.commands;

public record CompletePaymentCommand(Long paymentId, String transactionReference) {
}
