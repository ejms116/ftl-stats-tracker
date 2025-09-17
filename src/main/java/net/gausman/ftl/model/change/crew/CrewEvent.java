package net.gausman.ftl.model.change.crew;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;

public class CrewEvent extends Event {
    // this refers to the crew position in our ship status NOT the position in the save file
    private Integer crewPosition;

    public CrewEvent(){}

    public CrewEvent(SavedGameParser.StoreItemType itemType, Constants.EventType eventType, int amount, int scrap, String text, Jump jump){
        super(itemType, eventType, amount, scrap,  text, jump);
    }

    public Integer getCrewPosition() {
        return crewPosition;
    }

    public void setCrewPosition(Integer crewPosition) {
        this.crewPosition = crewPosition;
    }

}
