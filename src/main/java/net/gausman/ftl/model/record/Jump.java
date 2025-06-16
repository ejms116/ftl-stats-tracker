package net.gausman.ftl.model.record;

import com.fasterxml.jackson.annotation.JsonBackReference;
import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.factory.EventFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Jump {
    private static int nextId = 0;

    private final int id;
    private final int totalBeaconsExplored;
    private final int currentBeaconId;
    private final NavigableMap<Integer,Event> events = new TreeMap<>();
    private List<SavedGameParser.EncounterState> encounterStates = new ArrayList<>();
    @JsonBackReference
    private Sector sector;

    public Jump(int totalBeaconsExplored, int currentBeaconId, Sector sector) {
        this.totalBeaconsExplored = totalBeaconsExplored;
        this.currentBeaconId = currentBeaconId;
        this.id = nextId;
        this.sector = sector;
        nextId++;
    }

    public Jump(SavedGameParser.SavedGameState gameState, Sector sector){
        this.totalBeaconsExplored = gameState.getTotalBeaconsExplored();
        this.currentBeaconId = gameState.getCurrentBeaconId();
        this.id = nextId;
        this.sector = sector;
        nextId++;
    }



    public List<SavedGameParser.EncounterState> getEncounterStates() {
        return encounterStates;
    }

    public int getTotalBeaconsExplored() {
        return totalBeaconsExplored;
    }

    public int getCurrentBeaconId() {
        return currentBeaconId;
    }

    public int getId() {
        return id;
    }

    public void addEvent(Event event){
        EventFactory.assignEventId(event);
        events.put(event.getId() , event);
    }

    public void addEvents(List<Event> events){
        EventFactory.assignEventIds(events);
        for (Event e: events){
            this.events.put(e.getId(), e);
        }
    }

    public NavigableMap<Integer, Event> getEvents() {
        return events;
    }

    public static void resetNextId(){
        nextId = 0;
    }

    public Sector getSector() {
        return sector;
    }
}
