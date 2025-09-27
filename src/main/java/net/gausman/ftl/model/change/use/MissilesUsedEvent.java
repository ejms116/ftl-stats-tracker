package net.gausman.ftl.model.change.use;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;

public class MissilesUsedEvent extends Event {
    public MissilesUsedEvent(Jump jump, int usedAmount) {
        super(Constants.EventDetailType.USE_MISSILE, jump);
        setResourceEffect(Constants.Resource.MISSILE, -usedAmount);
        addTag(Constants.EventTag.USE);
    }
}
