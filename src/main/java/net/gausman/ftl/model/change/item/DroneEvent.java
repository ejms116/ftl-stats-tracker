package net.gausman.ftl.model.change.item;

import net.blerf.ftl.parser.SavedGameParser;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.record.Jump;
import net.gausman.ftl.util.GausmanUtil;

public class DroneEvent extends ItemEvent {

    public DroneEvent(){};

    public DroneEvent(Jump jump, String itemId) {
        super(Constants.EventDetailType.DRONE, jump, itemId);
    }

    @Override
    public String getDisplayText(){
        return GausmanUtil.getTextToId(SavedGameParser.StoreItemType.DRONE, getItemId());
    }
}
