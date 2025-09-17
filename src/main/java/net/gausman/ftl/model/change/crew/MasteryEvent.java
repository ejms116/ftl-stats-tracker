package net.gausman.ftl.model.change.crew;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.record.Jump;

public class MasteryEvent extends CrewEvent {
    private Constants.Skill mastery;
    private int level;
    private boolean newValue;

    public MasteryEvent(){}

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
