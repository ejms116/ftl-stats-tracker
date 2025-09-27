package net.gausman.ftl.model.change.general;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;

public class ScrapCollectedEvent extends Event {
    public ScrapCollectedEvent(Jump jump){
        super(Constants.EventDetailType.SCRAP_COLLECTED, jump);
    }
}
