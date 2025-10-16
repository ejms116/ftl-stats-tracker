package net.gausman.ftl.model.change.crew;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.Crew;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.record.Jump;
import net.gausman.ftl.util.GausmanUtil;

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

        if (getCrewPosition() == null){
            log.info("Skill event: crew position null");
            return;
        }

        if (getCrewPosition() >= model.getCrewList().size() || getCrewPosition() < 0){
            log.error(String.format("Skill event: crew position %s out of bounds %s", getCrewPosition(), model.getCrewList().size()));
            return;
        }

        Crew crewToChange = model.getCrewList().get(getCrewPosition());

        if (crewToChange == null){
            log.error("Crew for crew skill event not found. Position: " + getCrewPosition());
            return;
        }

        int attributeValueBefore = (int) model.getValueInCrewByAttributename(crewToChange, skillString);

        if (apply){
            model.setValueInCrewByAttributename(crewToChange, skillString, attributeValueBefore + getAmount());
        } else {
            model.setValueInCrewByAttributename(crewToChange, skillString, attributeValueBefore);
        }
    }
}
