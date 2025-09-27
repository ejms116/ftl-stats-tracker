package net.gausman.ftl.model.change.crew;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.record.Jump;

public class CrewRenameEvent extends CrewEvent {
    private String oldName;
    private String newName;

    public CrewRenameEvent(){}

    public CrewRenameEvent(Jump jump, String newName, String oldName){
        super(Constants.EventDetailType.CREW_RENAME, jump);
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
}
