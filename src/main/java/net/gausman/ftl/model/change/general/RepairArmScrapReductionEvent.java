package net.gausman.ftl.model.change.general;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;

public class RepairArmScrapReductionEvent extends Event {
    public RepairArmScrapReductionEvent() {}

    public RepairArmScrapReductionEvent(Jump jump){
        super(Constants.EventDetailType.REPAIR_ARM_SCRAP_REDUCTION, jump);
        addTag(Constants.EventTag.REWARD);
    }

    @Override
    public void applyEventToShipStatusModel(ShipStatusModel model, boolean apply) {
        super.applyEventToShipStatusModel(model, apply);
        int mult = apply ? 1 : -1;

        model.getSectorMetrics().update(
                getJump().getSector(),
                Constants.ScrapOrigin.REPAIR_ARM,
                mult*getResourceEffects().getOrDefault(Constants.Resource.SCRAP,0)
        );
    }
}
