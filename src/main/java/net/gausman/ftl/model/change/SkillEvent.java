package net.gausman.ftl.model.change;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.record.Jump;

public class SkillEvent extends CrewEvent {
    private final Constants.Skill skill;
    public SkillEvent(Constants.Skill skill, int amount, Jump jump){
        super(SavedGameParser.StoreItemType.CREW, Constants.EventType.SKILL, amount, 0,"skill", jump);
        this.skill = skill;
    }

    public Constants.Skill getSkill() {
        return skill;
    }
}
