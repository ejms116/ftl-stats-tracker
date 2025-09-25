package net.gausman.ftl.model.change.crew;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.record.Jump;

public class CrewRenameEvent extends CrewEvent {
    private String oldName;
    private String newName;

    public CrewRenameEvent(){}

    public CrewRenameEvent(String text, Jump jump, String newName, String oldName){
        super(text, jump);
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
