package net.gausman.ftl.model.change.crew;


import net.gausman.ftl.model.Crew;
import net.gausman.ftl.model.record.Jump;

public class NewCrewEvent extends CrewEvent {
    private Crew crew;

    public NewCrewEvent(){};

    public NewCrewEvent(String text, Jump jump) {
        super(text, jump);
    }

    public Crew getCrew() {
        return crew;
    }

    public void setCrew(Crew crew) {
        this.crew = crew;
    }
}
