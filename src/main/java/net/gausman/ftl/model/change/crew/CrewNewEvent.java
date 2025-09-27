package net.gausman.ftl.model.change.crew;


import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.Crew;
import net.gausman.ftl.model.record.Jump;

public class CrewNewEvent extends CrewEvent {
    private Crew crew;

    public CrewNewEvent(){};

    public CrewNewEvent(Jump jump) {
        super(Constants.EventDetailType.CREW_NEW, jump);
    }

    public Crew getCrew() {
        return crew;
    }

    public void setCrew(Crew crew) {
        this.crew = crew;
        setDisplayText(String.format("%s - %s", crew.getCrewType().name(), crew.getName()));
    }
}
