package net.gausman.ftl.model.change.crew;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;

public class CrewEvent extends Event {
    // this refers to the crew position in our ship status NOT the position in the save file
    private String crewId;

    public CrewEvent(){}

    public CrewEvent(Constants.EventDetailType eventDetailType, Jump jump){
        super(eventDetailType, jump);
    }

    public String getCrewId() {
        return crewId;
    }

    public void setCrewId(String crewId) {
        this.crewId = crewId;
    }
}
