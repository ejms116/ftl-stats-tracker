package net.gausman.ftl.model.record;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;

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

    @Override
    public String getDisplayText(){
        return String.format("%s - Mastery %s L%s: %s", getCrew().getName(), mastery.toString(), level, newValue);
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
