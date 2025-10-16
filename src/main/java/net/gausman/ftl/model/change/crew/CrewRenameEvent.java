package net.gausman.ftl.model.change.crew;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.Crew;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.record.Jump;
import net.gausman.ftl.util.GausmanUtil;

public class CrewRenameEvent extends CrewEvent {
    private String oldName;
    private String newName;

    public CrewRenameEvent(){}

    public CrewRenameEvent(Jump jump, String newName, String oldName){
        super(Constants.EventDetailType.CREW_RENAME, jump);
        addTag(Constants.EventTag.STAT);
        this.oldName = oldName;
        this.newName = newName;
        this.setDisplayText(String.format("%s renamed to %s", oldName, newName));
    }

    public String getOldName() {
        return oldName;
    }

    public String getNewName() {
        return newName;
    }

    @Override
    public void applyEventToShipStatusModel(ShipStatusModel model, boolean apply) {
        super.applyEventToShipStatusModel(model, apply);
        if (getCrewPosition() == null){
            log.info("Rename event: crew position null");
            return;
        }

        if (getCrewPosition() >= model.getCrewList().size() || getCrewPosition() < 0){
            log.error(String.format("Rename event: crew position %s out of bounds %s", getCrewPosition(), model.getCrewList().size()));
            return;
        }

        Crew crewToChange = model.getCrewList().get(getCrewPosition());

        if (crewToChange == null){
            log.error("Crew for crew rename event not found. Position: " + getCrewPosition());
            return;
        }

        if (apply){
            crewToChange.setName(getNewName());
        } else {
            crewToChange.setName(getOldName());
        }
    }
}
