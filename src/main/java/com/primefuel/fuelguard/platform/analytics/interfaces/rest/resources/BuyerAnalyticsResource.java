package com.primefuel.fuelguard.platform.analytics.interfaces.rest.resources;

import com.primefuel.fuelguard.platform.analytics.domain.model.valueobjects.MonthlyAmount;
import java.util.List;

public record BuyerAnalyticsResource(Long companyId, long totalOrders, double totalSpent,
                                     long completedPayments, long pendingPayments,
                                     List<MonthlyAmount> monthlySpending) {
}
