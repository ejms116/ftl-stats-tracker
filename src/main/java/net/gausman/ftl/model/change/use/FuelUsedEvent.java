package net.gausman.ftl.model.change.use;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;

public class FuelUsedEvent extends Event {
    public FuelUsedEvent(Jump jump) {
        super(Constants.EventDetailType.USE_FUEL, jump);
        setResourceEffect(Constants.Resource.FUEL, -1);
        addTag(Constants.EventTag.USE);
    }
}
