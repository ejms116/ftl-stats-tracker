package net.gausman.ftl.model.change.other;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;

public class RepairEvent extends Event {
    public RepairEvent(Jump jump){
        super(Constants.EventDetailType.REPAIR, jump);
        addTag(Constants.EventTag.BUY);
    }
}
