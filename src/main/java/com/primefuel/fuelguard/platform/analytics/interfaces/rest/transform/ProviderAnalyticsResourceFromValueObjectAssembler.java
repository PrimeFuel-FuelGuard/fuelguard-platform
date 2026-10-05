package com.primefuel.fuelguard.platform.analytics.interfaces.rest.transform;

import com.primefuel.fuelguard.platform.analytics.domain.model.valueobjects.ProviderAnalytics;
import com.primefuel.fuelguard.platform.analytics.interfaces.rest.resources.ProviderAnalyticsResource;

public final class ProviderAnalyticsResourceFromValueObjectAssembler {

    private ProviderAnalyticsResourceFromValueObjectAssembler() {}

    public static ProviderAnalyticsResource toResourceFromValueObject(ProviderAnalytics analytics) {
        return new ProviderAnalyticsResource(
                analytics.providerId(),
                analytics.totalOrders(),
                analytics.confirmedOrders(),
                analytics.cancelledOrders(),
                analytics.totalRevenue(),
                analytics.monthlyRevenue(),
                analytics.pendingOrders(),
                analytics.totalFuelSoldLitres(),
                analytics.salesTrend());
    }
}
