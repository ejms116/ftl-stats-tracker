package net.gausman.ftl.model.run;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;

import java.time.Instant;

public class FTLRunEvent {
    public static int nextEventNumber = 0;
    private Instant ts;
    private int eventNumber;
    private SavedGameParser.StoreItemType itemType;
    private Constants.EventType type;
    private int amount;
    private int cost;
    private String id;

    public FTLRunEvent(){
        ts = Instant.now();
        eventNumber = nextEventNumber;
        nextEventNumber++;
    }

    public FTLRunEvent(SavedGameParser.StoreItemType itemType, Constants.EventType type, int amount, int cost, String id){
        this.ts = Instant.now();
        this.itemType = itemType;
        this.type = type;
        this.amount = amount;
        this.cost = cost;
        this.id = id;
        eventNumber = nextEventNumber;
        nextEventNumber++;
    }

    public Instant getTs() {
        return ts;
    }

    public int getEventNumber(){
        return eventNumber;
    }

    public SavedGameParser.StoreItemType getItemType() {
        return itemType;
    }

    public void setItemType(SavedGameParser.StoreItemType itemType) {
        this.itemType = itemType;
    }

    public Constants.EventType getType() {
        return type;
    }

    public void setType(Constants.EventType type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public int getCost() {
        return cost;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setEventNumber(int eventNumber){
        this.eventNumber = eventNumber;
    }

    public void setId(String id) {
        this.id = id;
    }

}
