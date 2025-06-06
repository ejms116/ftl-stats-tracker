package net.gausman.ftl.model.run;

import net.blerf.ftl.parser.SavedGameParser;

import java.util.ArrayList;
import java.util.List;

public class FTLJump {
    private int totalBeaconsExplored;
    private int currentBeaconId;
    private int jumpNumber = 0;
    private List<FTLRunEvent> events = new ArrayList<>();

    public FTLJump(){

    }

    public FTLJump(int totalBeaconsExplored, int currentBeaconId, int jumpNumber) {
        this.totalBeaconsExplored = totalBeaconsExplored;
        this.currentBeaconId = currentBeaconId;
        this.jumpNumber = jumpNumber;
    }

    public FTLJump(SavedGameParser.SavedGameState gameState, int jumpNumber){
        this.totalBeaconsExplored = gameState.getTotalBeaconsExplored();
        this.currentBeaconId = gameState.getCurrentBeaconId();
        this.jumpNumber = jumpNumber;
    }


    public int getTotalBeaconsExplored() {
        return totalBeaconsExplored;
    }

    public int getCurrentBeaconId() {
        return currentBeaconId;
    }

    public int getJumpNumber() {
        return jumpNumber;
    }

    public void addEvent(FTLRunEvent event){
        events.add(event);
    }

    public void addEvents(List<FTLRunEvent> events){
        this.events.addAll(events);
    }

    public List<FTLRunEvent> getEvents() {
        return events;
    }
}
