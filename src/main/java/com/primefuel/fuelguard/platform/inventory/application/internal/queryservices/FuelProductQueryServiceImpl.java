package com.primefuel.fuelguard.platform.inventory.application.internal.queryservices;

import com.primefuel.fuelguard.platform.inventory.application.queryservices.FuelProductQueryService;
import com.primefuel.fuelguard.platform.inventory.domain.model.aggregates.FuelProduct;
import com.primefuel.fuelguard.platform.inventory.domain.model.queries.GetAllFuelProductsQuery;
import com.primefuel.fuelguard.platform.inventory.domain.model.queries.GetFuelProductByIdQuery;
import com.primefuel.fuelguard.platform.inventory.domain.model.queries.GetFuelProductsByProviderIdQuery;
import com.primefuel.fuelguard.platform.inventory.domain.repositories.FuelProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FuelProductQueryServiceImpl implements FuelProductQueryService {

    private final FuelProductRepository fuelProductRepository;

    public FuelProductQueryServiceImpl(FuelProductRepository fuelProductRepository) {
        this.fuelProductRepository = fuelProductRepository;
    }

    @Override
    public Optional<FuelProduct> handle(GetFuelProductByIdQuery query) {
        return fuelProductRepository.findById(query.fuelProductId());
    }

    @Override
    public List<FuelProduct> handle(GetAllFuelProductsQuery query) {
        return fuelProductRepository.findAll();
    }

    @Override
    public List<FuelProduct> handle(GetFuelProductsByProviderIdQuery query) {
        return fuelProductRepository.findByProviderId(query.providerId());
    }
}
