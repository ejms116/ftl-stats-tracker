package net.gausman.ftl.model.change;


import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.Crew;
import net.gausman.ftl.model.record.Jump;

public class NewCrewEvent extends CrewEvent {
    private Crew crew;
    public NewCrewEvent(SavedGameParser.StoreItemType itemType, Constants.EventType eventType, int amount, int scrap, String text, Jump jump) {
        super(itemType, eventType, amount, scrap, text, jump);
    }

    public Crew getCrew() {
        return crew;
    }

    public void setCrew(Crew crew) {
        this.crew = crew;
    }
}
