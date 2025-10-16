package net.gausman.ftl.model.change.crew;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.Crew;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.record.Jump;
import net.gausman.ftl.util.GausmanUtil;

import java.util.Optional;

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

        Optional<Crew> crewToChange = model.getCrewList().stream()
                .filter(c -> c.getReferenceId().equals(getCrewId()))
                .findFirst();

        crewToChange.ifPresent(c -> {
            String masteryString = GausmanUtil.convertMasteryToAttributename(getMastery(), getLevel());
            if (apply){
                model.setValueInCrewByAttributename(c, masteryString, getNewValue());
            } else {
                model.setValueInCrewByAttributename(c, masteryString, !getNewValue());
            }
        });
    }
}
