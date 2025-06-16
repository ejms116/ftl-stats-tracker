package net.gausman.ftl.model.record;

import com.fasterxml.jackson.annotation.JsonBackReference;
import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.util.GausmanUtil;

import java.time.Instant;

public class Event {
    private int id;
    private final Instant ts;
    private SavedGameParser.StoreItemType itemType; // Category
    private Constants.EventType eventType; // Type
    private int amount;
    private int scrap;
    private String text;
    @JsonBackReference
    private Jump jump;

    public Event(){
        ts = Instant.now();
    }

    public Event(SavedGameParser.StoreItemType itemType, Constants.EventType eventType, int amount, int scrap, String text, Jump jump){
        this.ts = Instant.now();
        this.itemType = itemType;
        this.eventType = eventType;
        this.amount = amount;
        this.scrap = scrap;
        this.text = text;
        this.jump = jump;
    }

    public String getDisplayText(){
        return GausmanUtil.getTextToId(itemType, text);
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

    public int getScrap() {
        return scrap;
    }

    public int getScrapChange(){
        int result = 0;
        switch (eventType){
            case REWARD -> result = 0;
            case START -> {
                if (this.itemType.equals(SavedGameParser.StoreItemType.GENERAL)){
                    result = scrap;
                } else {
                    result = 0;
                }
            }
            case GENERAL -> {
                GeneralEvent ge = (GeneralEvent) this;
                if (ge.getGeneral().equals(Constants.General.SCRAP_COLLECTED)){
                    result = scrap;
                }
            }
            case BUY, UPGRADE -> result = -scrap;
            case SELL -> result = scrap/2;
            default -> result = 0;
        };
        return result;

    }


    public void setScrap(int scrap) {
        this.scrap = scrap;
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
