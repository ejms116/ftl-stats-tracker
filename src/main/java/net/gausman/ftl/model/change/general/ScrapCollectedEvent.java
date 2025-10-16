package net.gausman.ftl.model.change.general;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;

public class ScrapCollectedEvent extends Event {

    public ScrapCollectedEvent() {}

    public ScrapCollectedEvent(Jump jump){
        super(Constants.EventDetailType.SCRAP_COLLECTED, jump);
    }

    @Override
    public void applyEventToShipStatusModel(ShipStatusModel model, boolean apply) {
        super.applyEventToShipStatusModel(model, apply);
        int mult = apply ? 1 : -1;


        model.getSectorMetrics().update(
                getJump().getSector(),
                Constants.ScrapOrigin.NORMAL,
                mult*getResourceEffects().getOrDefault(Constants.Resource.SCRAP,0)
        );
    }
}
