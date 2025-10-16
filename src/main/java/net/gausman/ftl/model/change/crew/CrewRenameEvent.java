package net.gausman.ftl.model.change.crew;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.Crew;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.record.Jump;
import net.gausman.ftl.util.GausmanUtil;

import java.util.Optional;

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

        Optional<Crew> crewToChange = model.getCrewList().stream()
                .filter(c -> c.getReferenceId().equals(getCrewId()))
                .findFirst();

        crewToChange.ifPresent(c -> {
            if (apply){
                c.setName(getNewName());
            } else {
                c.setName(getOldName());
            }
        });
    }
}
