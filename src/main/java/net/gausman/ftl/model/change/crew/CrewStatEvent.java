package net.gausman.ftl.model.change.crew;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.record.Jump;

public class CrewStatEvent extends CrewEvent {
    private Constants.Stats stat;
    private int amount;

    public CrewStatEvent(){};

    public CrewStatEvent(Jump jump, Constants.Stats stat, int amount){
        super(Constants.EventDetailType.CREW_STAT, jump);
        this.stat = stat;
        this.amount = amount;
    }

    public Constants.Stats getStat() {
        return stat;
    }

    public int getAmount() {
        return amount;
    }
}
