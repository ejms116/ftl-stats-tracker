package net.gausman.ftl.model.change.other;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;

public class ScrapDiffErrorEvent extends Event {
    public ScrapDiffErrorEvent(Jump jump){
        super(Constants.EventDetailType.SCRAP_DIFF_ERROR, jump);
    }
}
