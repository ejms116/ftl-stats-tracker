package net.gausman.ftl.model.change.general;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;

public class ShipsDestroyedEvent extends Event {
    public ShipsDestroyedEvent() {}

    public ShipsDestroyedEvent(Jump jump){
        super(Constants.EventDetailType.SHIPS_DESTROYED, jump);
        addTag(Constants.EventTag.STAT);
    }
}
