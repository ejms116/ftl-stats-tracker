package net.gausman.ftl.model.change;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.record.Jump;

public class MasteryEvent extends CrewEvent {
    private final Constants.Skill mastery;
    private final int level;
    private final boolean newValue;

    public MasteryEvent(Constants.Skill mastery, int level, boolean newValue, Jump jump){
        super(SavedGameParser.StoreItemType.CREW, Constants.EventType.MASTERY, 0, 0,"mastery", jump);
        this.mastery = mastery;
        this.level = level;
        this.newValue = newValue;
    }

    public Constants.Skill getMastery() {
        return mastery;
    }

    public int getLevel() {
        return level;
    }

    public boolean getNewValue() {
        return newValue;
    }
}
