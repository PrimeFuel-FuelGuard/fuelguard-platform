package com.primefuel.fuelguard.platform.inventory.interfaces.rest.transform;

import com.primefuel.fuelguard.platform.inventory.domain.model.commands.UpdateFuelProductStockCommand;
import com.primefuel.fuelguard.platform.inventory.interfaces.rest.resources.UpdateFuelProductStockResource;

public final class UpdateFuelProductStockCommandFromResourceAssembler {

    private UpdateFuelProductStockCommandFromResourceAssembler() {
    }

    public static UpdateFuelProductStockCommand toCommandFromResource(Long fuelProductId,
                                                                      UpdateFuelProductStockResource resource) {
        return new UpdateFuelProductStockCommand(fuelProductId, resource.newStock());
    }
}