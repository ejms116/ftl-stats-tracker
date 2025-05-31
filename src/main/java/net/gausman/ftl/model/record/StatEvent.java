package net.gausman.ftl.model.record;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;

public class StatEvent extends CrewEvent {
    private Constants.Stats stat;

    public StatEvent(Constants.Stats stat, int amount, Jump jump){
        super(SavedGameParser.StoreItemType.CREW, Constants.EventType.STAT, amount, 0,"stat", jump);
        this.stat = stat;
    }

    @Override
    public String getDisplayText(){
        return switch (stat){
            case REPAIRS -> String.format("%s - %s: %s", getCrew().getName(), stat.toString(), getCrew().getRepairs() + getAmount());
            case COMBAT_KILLS -> String.format("%s - %s: %s", getCrew().getName(), stat.toString(), getCrew().getCombatKills() + getAmount());
            case PILOTED_EVASIONS -> String.format("%s - %s: %s", getCrew().getName(), stat.toString(), getCrew().getPilotedEvasions() + getAmount());
            case JUMPS_SURVIVED -> String.format("%s - %s: %s", getCrew().getName(), stat.toString(), getCrew().getJumpsSurvived() + getAmount());
            case SKILL_MASTERIES_EARNED -> String.format("%s - %s: %s", getCrew().getName(), stat.toString(), getCrew().getSkillMasteriesEarned() + getAmount());
        };
    }

    public Constants.Stats getStat() {
        return stat;
    }
}
