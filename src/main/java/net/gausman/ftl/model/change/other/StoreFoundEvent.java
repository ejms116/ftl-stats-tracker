package net.gausman.ftl.model.change.other;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;
import net.gausman.ftl.model.record.StoreInfo;

public class StoreFoundEvent extends Event {
    private SavedGameParser.StoreState storeState;
    public StoreFoundEvent(){}

    public StoreFoundEvent(Jump jump){
        super(Constants.EventDetailType.STORE_FOUND, jump);
        addTag(Constants.EventTag.STORE);
    }

    public SavedGameParser.StoreState getStoreState() {
        return storeState;
    }

    public void setStoreState(SavedGameParser.StoreState storeState) {
        this.storeState = storeState;
    }

    @Override
    public String getDisplayText(){
        return "New Store found";
    }

    @Override
    public void applyEventToShipStatusModel(ShipStatusModel model, boolean apply) {
        super.applyEventToShipStatusModel(model, apply);
        StoreInfo storeInfo = new StoreInfo(getJump().getSector().getId(), getJump().getCurrentBeaconId(), new SavedGameParser.StoreState(storeState), model.getResources().get(Constants.Resource.SCRAP));
        storeInfo.getVisitedOnJumps().add(getJump().getId());
        model.getSectorMetrics().update(
                getJump().getSector(),
                storeInfo,
                apply,
                getJump().getCurrentBeaconId()
        );
    }
}
