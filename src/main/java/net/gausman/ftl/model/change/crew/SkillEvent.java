package net.gausman.ftl.model.change.crew;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.record.Jump;

public class SkillEvent extends CrewEvent {
    private Constants.Skill skill;
    private int amount;

    public SkillEvent(){};

    public SkillEvent(String text, Jump jump, Constants.Skill skill, int amount){
        super(text, jump);
        this.skill = skill;
        this.amount = amount;
    }

    public Constants.Skill getSkill() {
        return skill;
    }

    public int getAmount() {
        return amount;
    }
}
