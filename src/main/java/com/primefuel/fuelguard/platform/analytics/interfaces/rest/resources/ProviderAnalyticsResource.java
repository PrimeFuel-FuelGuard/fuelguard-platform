package com.primefuel.fuelguard.platform.analytics.interfaces.rest.resources;

import com.primefuel.fuelguard.platform.analytics.domain.model.valueobjects.MonthlyAmount;
import com.primefuel.fuelguard.platform.analytics.domain.model.valueobjects.SalesTrendPoint;

import java.util.List;

public record ProviderAnalyticsResource(
        Long providerId,
        long totalOrders,
        long confirmedOrders,
        long cancelledOrders,
        double totalRevenue,
        List<MonthlyAmount> monthlyRevenue,
        long pendingOrders,
        double totalFuelSoldLitres,
        List<SalesTrendPoint> salesTrend) {}
