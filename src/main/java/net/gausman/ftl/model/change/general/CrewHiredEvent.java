package net.gausman.ftl.model.change.general;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;

public class CrewHiredEvent extends Event {

    public CrewHiredEvent() {}

    public CrewHiredEvent(Jump jump){
        super(Constants.EventDetailType.CREW_HIRED, jump);
        addTag(Constants.EventTag.STAT);
    }
}
