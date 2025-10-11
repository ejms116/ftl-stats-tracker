package net.gausman.ftl.model.change.resources;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;

public class DronesBoughtEvent extends Event {
    public static final int DRONES_PRICE_STORE = 8;

    public DronesBoughtEvent() {}

    public DronesBoughtEvent(Jump jump, int dronesBought) {
        super(Constants.EventDetailType.BUY_DRONE, jump);
        setResourceEffect(Constants.Resource.DRONE, dronesBought);
        setResourceEffect(Constants.Resource.SCRAP, -DRONES_PRICE_STORE * dronesBought);
        addTag(Constants.EventTag.BUY);
        setDisplayText("Bought drones");
    }
    @Override
    public void applyEventToShipStatusModel(ShipStatusModel model, boolean apply) {
        super.applyEventToShipStatusModel(model, apply);
        int mult = apply ? 1 : -1;

        model.getSectorMetrics().update(
                getJump().getSector(),
                Constants.ScrapUsedCategory.DRONES,
                mult*-getResourceEffects().getOrDefault(Constants.Resource.SCRAP,0)
        );
    }
}
