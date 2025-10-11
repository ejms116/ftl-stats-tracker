package net.gausman.ftl.model.change.other;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;

public class RepairEvent extends Event {
    public RepairEvent() {}

    public RepairEvent(Jump jump){
        super(Constants.EventDetailType.REPAIR, jump);
    }

    @Override
    public void applyEventToShipStatusModel(ShipStatusModel model, boolean apply) {
        super.applyEventToShipStatusModel(model, apply);
        int mult = apply ? 1 : -1;

        model.getSectorMetrics().update(
                getJump().getSector(),
                Constants.ScrapUsedCategory.REPAIR,
                mult*-getResourceEffects().getOrDefault(Constants.Resource.SCRAP,0)
        );
    }
}
