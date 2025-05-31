package net.gausman.ftl.model.record;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;

public class SkillEvent extends CrewEvent {
    private final Constants.Skill skill;
    public SkillEvent(Constants.Skill skill, int amount, Jump jump){
        super(SavedGameParser.StoreItemType.CREW, Constants.EventType.SKILL, amount, 0,"skill", jump);
        this.skill = skill;
    }


    @Override
    public String getDisplayText(){
        return switch (skill){
            case PILOT -> String.format("%s - %s Skill: %s", getCrew().getName(), skill, getCrew().getPilotSkill() + getAmount());
            case ENGINE -> String.format("%s - %s Skill: %s", getCrew().getName(), skill, getCrew().getEngineSkill() + getAmount());
            case SHIELD -> String.format("%s - %s Skill: %s", getCrew().getName(), skill, getCrew().getShieldSkill() + getAmount());
            case WEAPON -> String.format("%s - %s Skill: %s", getCrew().getName(), skill, getCrew().getWeaponSkill() + getAmount());
            case REPAIR -> String.format("%s - %s Skill: %s", getCrew().getName(), skill, getCrew().getRepairSkill() + getAmount());
            case COMBAT -> String.format("%s - %s Skill: %s", getCrew().getName(), skill, getCrew().getCombatSkill() + getAmount());
        };
    }

    public Constants.Skill getSkill() {
        return skill;
    }
}
