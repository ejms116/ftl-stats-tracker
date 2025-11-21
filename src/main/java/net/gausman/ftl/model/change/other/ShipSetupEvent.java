package net.gausman.ftl.model.change.other;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;

public class ShipSetupEvent extends Event {
    public ShipSetupEvent() {}

    public ShipSetupEvent(Jump jump){
        super(Constants.EventDetailType.SHIP_SETUP, jump);
    }

    @Override
    public void applyEventToShipStatusModel(ShipStatusModel model, boolean apply) {
        super.applyEventToShipStatusModel(model, apply);

        if (getResourceEffects().containsKey(Constants.Resource.SCRAP)){
            int mult = apply ? 1 : -1;
            int flow = mult * getResourceEffects().getOrDefault(Constants.Resource.SCRAP, 0);
            if (flow != 0){
                model.getSectorMetrics().updateFlows(1, Constants.Sankey.SCRAP_START.toString(), Constants.Sankey.SCRAP_AVAILABLE.toString(), flow);
            }
        }
    }
}
