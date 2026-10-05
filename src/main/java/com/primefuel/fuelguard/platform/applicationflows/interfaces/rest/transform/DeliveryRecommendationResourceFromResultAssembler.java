package com.primefuel.fuelguard.platform.applicationflows.interfaces.rest.transform;

import com.primefuel.fuelguard.platform.applicationflows.DeliveryRecommendation;
import com.primefuel.fuelguard.platform.applicationflows.interfaces.rest.resources.DeliveryRecommendationResource;

public final class DeliveryRecommendationResourceFromResultAssembler {
    private DeliveryRecommendationResourceFromResultAssembler() {}

    public static DeliveryRecommendationResource toResource(DeliveryRecommendation r) {
        return new DeliveryRecommendationResource(
                r.orderId(),
                r.recommended(),
                r.reason(),
                r.driverId(),
                r.driverName(),
                r.tankerId(),
                r.licensePlate(),
                r.tankerCapacityLitres(),
                r.requestedVolumeLitres(),
                r.windowStart(),
                r.windowEnd(),
                r.criterion());
    }
}
