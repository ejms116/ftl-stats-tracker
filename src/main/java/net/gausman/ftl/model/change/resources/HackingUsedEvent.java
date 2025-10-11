package net.gausman.ftl.model.change.resources;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;

public class HackingUsedEvent extends Event {
    public HackingUsedEvent() {}

    public HackingUsedEvent(Jump jump, int usedAmount) {
        super(Constants.EventDetailType.USE_HACKING, jump);
        setResourceEffect(Constants.Resource.DRONE, -usedAmount);
        addTag(Constants.EventTag.USE);
    }
}
