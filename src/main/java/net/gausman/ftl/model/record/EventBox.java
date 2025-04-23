package net.gausman.ftl.model.record;

import net.gausman.ftl.model.run.FTLRunEvent;

import java.util.List;

public class EventBox {
    private final List<Event> newJumpEvents;
    private final List<Event> lastJumpEvents;

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
}
