package com.primefuel.fuelguard.platform.fulfillment.domain.model.queries;

import java.time.LocalDate;

public record GetDeliveriesByProviderQuery(Long providerId, LocalDate date) {}
