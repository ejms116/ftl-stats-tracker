package net.gausman.ftl.model.change.other;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;

public class ResourceDiffErrorEvent extends Event {
    public ResourceDiffErrorEvent() {}

    public ResourceDiffErrorEvent(Jump jump){
        super(Constants.EventDetailType.RESOURCE_DIFF_ERROR, jump);
        addTag(Constants.EventTag.ERROR);
    }
}
