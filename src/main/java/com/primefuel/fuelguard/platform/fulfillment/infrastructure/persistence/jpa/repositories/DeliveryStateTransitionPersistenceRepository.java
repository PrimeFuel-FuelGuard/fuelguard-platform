package com.primefuel.fuelguard.platform.fulfillment.infrastructure.persistence.jpa.repositories;

import com.primefuel.fuelguard.platform.fulfillment.infrastructure.persistence.jpa.entities.DeliveryStateTransitionPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryStateTransitionPersistenceRepository
        extends JpaRepository<DeliveryStateTransitionPersistenceEntity, Long> {

    List<DeliveryStateTransitionPersistenceEntity> findByDeliveryIdOrderByIdAsc(Long deliveryId);
}
