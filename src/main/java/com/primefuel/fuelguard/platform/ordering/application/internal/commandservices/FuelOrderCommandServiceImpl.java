package com.primefuel.fuelguard.platform.ordering.application.internal.commandservices;

import com.primefuel.fuelguard.platform.equipment.application.queryservices.EquipmentQueryService;
import com.primefuel.fuelguard.platform.equipment.domain.model.queries.GetEquipmentByIdQuery;
import com.primefuel.fuelguard.platform.inventory.application.queryservices.FuelProductQueryService;
import com.primefuel.fuelguard.platform.inventory.domain.model.queries.GetFuelProductByIdQuery;
import com.primefuel.fuelguard.platform.ordering.application.commandservices.FuelOrderCommandService;
import com.primefuel.fuelguard.platform.ordering.domain.model.aggregates.FuelOrder;
import com.primefuel.fuelguard.platform.ordering.domain.model.commands.CancelFuelOrderCommand;
import com.primefuel.fuelguard.platform.ordering.domain.model.commands.ConfirmFuelOrderCommand;
import com.primefuel.fuelguard.platform.ordering.domain.model.commands.CreateFuelOrderCommand;
import com.primefuel.fuelguard.platform.ordering.domain.repositories.FuelOrderRepository;
import com.primefuel.fuelguard.platform.shared.application.result.ApplicationError;
import com.primefuel.fuelguard.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

@Service
public class FuelOrderCommandServiceImpl implements FuelOrderCommandService {

    private final FuelOrderRepository fuelOrderRepository;
    private final FuelProductQueryService fuelProductQueryService;
    private final EquipmentQueryService equipmentQueryService;

    public FuelOrderCommandServiceImpl(FuelOrderRepository fuelOrderRepository,
                                       FuelProductQueryService fuelProductQueryService,
                                       EquipmentQueryService equipmentQueryService) {
        this.fuelOrderRepository = fuelOrderRepository;
        this.fuelProductQueryService = fuelProductQueryService;
        this.equipmentQueryService = equipmentQueryService;
    }

    @Override
    public Result<FuelOrder, ApplicationError> handle(CreateFuelOrderCommand command) {
        var productResult = fuelProductQueryService.handle(new GetFuelProductByIdQuery(command.fuelProductId()));
        if (productResult.isEmpty()) {
            return Result.failure(ApplicationError.notFound("FuelProduct", command.fuelProductId().toString()));
        }
        var product = productResult.get();
        if (!product.getProviderId().equals(command.providerId())) {
            return Result.failure(ApplicationError.forbidden(
                    "FuelProduct %s does not belong to provider %s".formatted(
                            command.fuelProductId(), command.providerId())));
        }

        if (command.equipmentId() != null) {
            var equipmentResult = equipmentQueryService.handle(new GetEquipmentByIdQuery(command.equipmentId()));
            if (equipmentResult.isEmpty()) {
                return Result.failure(ApplicationError.notFound("Equipment", command.equipmentId().toString()));
            }
            if (!equipmentResult.get().getCompanyId().equals(command.companyId())) {
                return Result.failure(ApplicationError.forbidden(
                        "Equipment %s does not belong to company %s".formatted(
                                command.equipmentId(), command.companyId())));
            }
        }

        var totalPrice = product.getPricePerUnit() * command.requestedQuantity();
        var order = new FuelOrder(command, totalPrice);
        var saved = fuelOrderRepository.save(order);
        return Result.success(saved);
    }

    @Override
    public Result<FuelOrder, ApplicationError> handle(ConfirmFuelOrderCommand command) {
        var existing = fuelOrderRepository.findById(command.orderId());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("FuelOrder", command.orderId().toString()));
        }
        var order = existing.get();
        try {
            order.confirm();
        } catch (IllegalStateException exception) {
            return Result.failure(ApplicationError.conflict("FuelOrder", exception.getMessage()));
        }
        return Result.success(fuelOrderRepository.save(order));
    }

    @Override
    public Result<FuelOrder, ApplicationError> handle(CancelFuelOrderCommand command) {
        var existing = fuelOrderRepository.findById(command.orderId());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("FuelOrder", command.orderId().toString()));
        }
        var order = existing.get();
        try {
            order.cancel();
        } catch (IllegalStateException exception) {
            return Result.failure(ApplicationError.conflict("FuelOrder", exception.getMessage()));
        }
        return Result.success(fuelOrderRepository.save(order));
    }
}
