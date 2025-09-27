package net.gausman.ftl.model.change.crew;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.record.Jump;

public class CrewSkillEvent extends CrewEvent {
    private Constants.Skill skill;
    private int amount;

    public CrewSkillEvent(){};

    public CrewSkillEvent(Jump jump, Constants.Skill skill, int amount){
        super(Constants.EventDetailType.CREW_SKILL, jump);
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
