package net.gausman.ftl.model.change.other;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;

public class ResourceEvent extends Event {
    public ResourceEvent(Jump jump){
        super(Constants.EventDetailType.RESOURCE, jump);
    }

//    @Override
//    public void applyEventToShipStatusModel(ShipStatusModel model, boolean apply) {
//        super.applyEventToShipStatusModel(model, apply);
//        int mult = apply ? 1 : -1;
//
//        if (getTags().contains(Constants.EventTag.BUY)){
//            model.getSectorMetrics().update(
//                    getJump().getSector(),
//                    GausmanUtil.convertResourceToScrapUsedCategory()
//            );
//        }
//
//    }
}
