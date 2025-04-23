package net.gausman.ftl.model.record;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;

import java.time.Instant;

public class Event {
    private int id;
    private final Instant ts;
    private SavedGameParser.StoreItemType itemType; // Category
    private Constants.EventType eventType; // Type
    private int amount;
    private int cost;
    private String text;
    private Jump jump;



    public Event(){
        ts = Instant.now();
    }

    public Event(SavedGameParser.StoreItemType itemType, Constants.EventType eventType, int amount, int cost, String text, Jump jump){
        this.ts = Instant.now();
        this.itemType = itemType;
        this.eventType = eventType;
        this.amount = amount;
        this.cost = cost;
        this.text = text;
        this.jump = jump;
    }

    public void assignId(int id) {
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public Instant getTs() {
        return ts;
    }

    public SavedGameParser.StoreItemType getItemType() {
        return itemType;
    }

    public Constants.EventType getEventType() {
        return eventType;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Jump getJump(){
        return jump;
    }

}
