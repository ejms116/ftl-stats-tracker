package net.gausman.ftl.model.change.general;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;

public class ScrapRoundingDiffEvent extends Event {
    public ScrapRoundingDiffEvent() {}

    public ScrapRoundingDiffEvent(Jump jump){
        super(Constants.EventDetailType.DOUBLE_ARM_ROUNDING_DIFF, jump);
        addTag(Constants.EventTag.REWARD);
    }

    @Override
    public void applyEventToShipStatusModel(ShipStatusModel model, boolean apply) {
        super.applyEventToShipStatusModel(model, apply);
        int mult = apply ? 1 : -1;

        model.getSectorMetrics().update(
                getJump().getSector(),
                Constants.ScrapOrigin.ROUNDING_DIFF,
                mult*getResourceEffects().getOrDefault(Constants.Resource.SCRAP,0)
        );
    }
}
