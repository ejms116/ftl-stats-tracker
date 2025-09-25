package net.gausman.ftl.model.change.crew;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.record.Jump;

public class StatEvent extends CrewEvent {
    private Constants.Stats stat;
    private int amount;

    public StatEvent(){};

    public StatEvent(String text, Jump jump, Constants.Stats stat, int amount){
        super(text, jump);
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
