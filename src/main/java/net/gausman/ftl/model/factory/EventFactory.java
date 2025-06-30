package net.gausman.ftl.model.factory;

import net.gausman.ftl.model.change.Event;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EventFactory {
    private static final AtomicInteger nextEventId = new AtomicInteger(0);

    public static void assignEventId(Event event){
        event.assignId(nextEventId.getAndIncrement());
    }

    public static void assignEventIds(List<Event> events){
        for (Event e: events){
            e.assignId(nextEventId.getAndIncrement());
        }
    }

    public static void resetNextEventId(){
        nextEventId.set(0);
    }
}
