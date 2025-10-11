package net.gausman.ftl.model.change.resources;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;

public class DronesUsedEvent extends Event {
    public DronesUsedEvent() {}

    public DronesUsedEvent(Jump jump, int usedAmount) {
        super(Constants.EventDetailType.USE_DRONE, jump);
        setResourceEffect(Constants.Resource.DRONE, -usedAmount);
        addTag(Constants.EventTag.USE);
    }
}
