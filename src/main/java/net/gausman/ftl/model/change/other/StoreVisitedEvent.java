package net.gausman.ftl.model.change.other;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;

public class StoreVisitedEvent extends Event {
    public StoreVisitedEvent(){}

    public StoreVisitedEvent(Jump jump){
        super(Constants.EventDetailType.STORE_VISITED, jump);
        addTag(Constants.EventTag.STORE);
    }

    @Override
    public String getDisplayText(){
        return "Store visited again";
    }

    @Override
    public void applyEventToShipStatusModel(ShipStatusModel model, boolean apply) {
        super.applyEventToShipStatusModel(model, apply);
        model.getSectorMetrics().getInfo(getJump().getSector()).getStoreInfoMap().get(getJump().getCurrentBeaconId()).getVisitedOnJumps().add(getJump().getId());
    }
}
