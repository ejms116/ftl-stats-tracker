package net.gausman.ftl.model.change.resources;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;

public class EventRewardEvent extends Event {
    public EventRewardEvent() {}

    public EventRewardEvent(Jump jump){
        super(Constants.EventDetailType.EVENT_REWARD, jump);
    }

    @Override
    public void applyEventToShipStatusModel(ShipStatusModel model, boolean apply) {
        super.applyEventToShipStatusModel(model, apply);

        if (getResourceEffects().containsKey(Constants.Resource.SCRAP)){
            int mult = apply ? 1 : -1;
            model.getSectorMetrics().update(
                    getJump().getSector(),
                    Constants.ScrapOrigin.NORMAL,
                    mult*getResourceEffects().getOrDefault(Constants.Resource.SCRAP,0)
            );
        }
    }
}
