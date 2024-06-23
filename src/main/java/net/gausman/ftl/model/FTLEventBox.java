package net.gausman.ftl.model;

import net.gausman.ftl.model.run.FTLRunEvent;

import java.util.List;

public class FTLEventBox {
    private final List<FTLRunEvent> newJumpEvents;
    private final List<FTLRunEvent> lastJumpEvents;

    public FTLEventBox(List<FTLRunEvent> newJumpEvents, List<FTLRunEvent> lastJumpEvents) {
        this.newJumpEvents = newJumpEvents;
        this.lastJumpEvents = lastJumpEvents;
    }

    public List<FTLRunEvent> getNewJumpEvents() {
        return newJumpEvents;
    }

    public List<FTLRunEvent> getLastJumpEvents() {
        return lastJumpEvents;
    }
}
