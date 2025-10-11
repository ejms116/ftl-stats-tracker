package net.gausman.ftl.model.change.resources;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;

public class ResourcesReceivedEvent extends Event {
    public ResourcesReceivedEvent() {}

    public ResourcesReceivedEvent(Jump jump){
        super(Constants.EventDetailType.RESOURCES_RECEIVED, jump);
    }
}
