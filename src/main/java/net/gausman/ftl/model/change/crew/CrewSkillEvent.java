package net.gausman.ftl.model.change.crew;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.Crew;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.record.Jump;
import net.gausman.ftl.util.GausmanUtil;

import java.util.Optional;

public class CrewSkillEvent extends CrewEvent {
    private Constants.Skill skill;
    private int amount;

    public CrewSkillEvent(){};

    public CrewSkillEvent(Jump jump, Constants.Skill skill, int amount){
        super(Constants.EventDetailType.CREW_SKILL, jump);
        addTag(Constants.EventTag.STAT);
        this.skill = skill;
        this.amount = amount;
    }

    public Constants.Skill getSkill() {
        return skill;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public void applyEventToShipStatusModel(ShipStatusModel model, boolean apply) {
        super.applyEventToShipStatusModel(model, apply);

        String skillString = GausmanUtil.convertSkillToAttributename(getSkill());
        Optional<Crew> crewToChange = model.getCrewList().stream()
                .filter(c -> c.getReferenceId().equals(getCrewId()))
                .findFirst();

        crewToChange.ifPresent(c -> {
            int attributeValueBefore = (int) model.getValueInCrewByAttributename(c, skillString);

            if (apply){
                model.setValueInCrewByAttributename(c, skillString, attributeValueBefore + getAmount());
            } else {
                model.setValueInCrewByAttributename(c, skillString, attributeValueBefore);
            }
        });


    }
}
