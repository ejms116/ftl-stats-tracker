package net.gausman.ftl.model.change.resources;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;

public class FuelBoughtEvent extends Event {
    public static final int FUEL_PRICE_STORE = 3;

    public FuelBoughtEvent() {}

    public FuelBoughtEvent(Jump jump, int fuelBought) {
        super(Constants.EventDetailType.BUY_FUEL, jump);
        setResourceEffect(Constants.Resource.FUEL, fuelBought);
        setResourceEffect(Constants.Resource.SCRAP, -FUEL_PRICE_STORE * fuelBought);
        addTag(Constants.EventTag.BUY);
        setDisplayText("Bought fuel");
    }

    @Override
    public void applyEventToShipStatusModel(ShipStatusModel model, boolean apply) {
        super.applyEventToShipStatusModel(model, apply);
        int mult = apply ? 1 : -1;

        model.getSectorMetrics().update(
                getJump().getSector(),
                Constants.ScrapUsedCategory.FUEL,
                mult*-getResourceEffects().getOrDefault(Constants.Resource.SCRAP,0)
        );
    }
}
