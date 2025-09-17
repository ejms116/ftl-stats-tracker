package net.gausman.ftl.model.change.crew;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.record.Jump;

public class StatEvent extends CrewEvent {
    private Constants.Stats stat;

    public StatEvent(){};

    public StatEvent(Constants.Stats stat, int amount, Jump jump){
        super(SavedGameParser.StoreItemType.CREW, Constants.EventType.STAT, amount, 0,"stat", jump);
        this.stat = stat;
    }

    public Constants.Stats getStat() {
        return stat;
    }
}
