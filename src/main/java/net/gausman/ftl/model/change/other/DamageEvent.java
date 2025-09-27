package net.gausman.ftl.model.change.other;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;

public class DamageEvent extends Event {
    public DamageEvent(Jump jump){
        super(Constants.EventDetailType.DAMAGE, jump);
    }
}
