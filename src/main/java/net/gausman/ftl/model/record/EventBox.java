package net.gausman.ftl.model.record;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.run.FTLRunEvent;

import java.util.List;

public class EventBox {
    private final List<Event> newJumpEvents;
    private final List<Event> lastJumpEvents;
    private SavedGameParser.EncounterState encounterState;


    public EventBox(List<Event> newJumpEvents, List<Event> lastJumpEvents) {
        this.newJumpEvents = newJumpEvents;
        this.lastJumpEvents = lastJumpEvents;
    }

    public List<Event> getNewJumpEvents() {
        return newJumpEvents;
    }

    public List<Event> getLastJumpEvents() {
        return lastJumpEvents;
    }

    public void setEncounterState(SavedGameParser.EncounterState encounterState) {
        this.encounterState = encounterState;
    }

    public SavedGameParser.EncounterState getEncounterState() {
        return encounterState;
    }
}
