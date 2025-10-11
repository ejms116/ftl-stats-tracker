package net.gausman.ftl.model.change.other;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;

public class ShipSetupEvent extends Event {
    public ShipSetupEvent() {}

    public ShipSetupEvent(Jump jump){
        super(Constants.EventDetailType.SHIP_SETUP, jump);
    }
}
