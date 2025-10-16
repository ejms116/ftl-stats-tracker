package net.gausman.ftl.model.change.crew;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.Crew;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.record.Jump;
import net.gausman.ftl.util.GausmanUtil;

public class CrewMasteryEvent extends CrewEvent {
    private Constants.Skill mastery;
    private int level;
    private boolean newValue;

    public CrewMasteryEvent(){}

    public CrewMasteryEvent(Jump jump, Constants.Skill mastery, int level, boolean newValue){
        super (Constants.EventDetailType.CREW_MASTERY, jump);
        addTag(Constants.EventTag.STAT);
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

    @Override
    public void applyEventToShipStatusModel(ShipStatusModel model, boolean apply) {
        super.applyEventToShipStatusModel(model, apply);

        String masteryString = GausmanUtil.convertMasteryToAttributename(getMastery(), getLevel());
        if (getCrewPosition() == null){
            log.info("Mastery event: crew position null");
            return;
        }

        if (getCrewPosition() >= model.getCrewList().size() || getCrewPosition() < 0){
            log.error(String.format("Mastery event: crew position %s out of bounds %s", getCrewPosition(), model.getCrewList().size()));
            return;
        }

        Crew crewToChange = model.getCrewList().get(getCrewPosition());

        if (crewToChange == null){
            log.error("Crew for crew mastery event not found. Position: " + getCrewPosition());
            return;
        }

        if (apply){
            model.setValueInCrewByAttributename(crewToChange, masteryString, getNewValue());
        } else {
            model.setValueInCrewByAttributename(crewToChange, masteryString, !getNewValue());
        }
    }
}
