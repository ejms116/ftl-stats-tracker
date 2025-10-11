package net.gausman.ftl.model.change.general;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;

public class SRAExtraScrapEvent extends Event {
    public SRAExtraScrapEvent() {}

    public SRAExtraScrapEvent(Jump jump){
        super(Constants.EventDetailType.SRA_EXTRA_SCRAP, jump);
        addTag(Constants.EventTag.REWARD);
    }

    @Override
    public void applyEventToShipStatusModel(ShipStatusModel model, boolean apply) {
        super.applyEventToShipStatusModel(model, apply);
        int mult = apply ? 1 : -1;

        model.getSectorMetrics().update(
                getJump().getSector(),
                Constants.ScrapOrigin.SRA,
                mult*getResourceEffects().getOrDefault(Constants.Resource.SCRAP,0)
        );
    }
}
