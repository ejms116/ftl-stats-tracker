package net.gausman.ftl.model.change.item;

import net.blerf.ftl.parser.SavedGameParser;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;
import net.gausman.ftl.util.GausmanUtil;

public class DroneEvent extends ItemEvent {

    public DroneEvent(){};

    public DroneEvent(String text, Jump jump, String itemId) {
        super(text, jump, itemId);
    }

    @Override
    public String getDisplayText(){
        return GausmanUtil.getTextToId(SavedGameParser.StoreItemType.DRONE, getItemId());
    }
}
