package net.gausman.ftl.model.change.general;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;

public class BeaconsExploredEvent extends Event {
    public BeaconsExploredEvent(Jump jump){
        super(Constants.EventDetailType.BEACONS_EXPLORED, jump);
    }
}
