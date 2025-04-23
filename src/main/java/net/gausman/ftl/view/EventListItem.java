package net.gausman.ftl.view;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.run.FTLJump;
import net.gausman.ftl.model.run.FTLRun;
import net.gausman.ftl.model.run.FTLRunEvent;
import net.gausman.ftl.model.run.FTLSector;
import net.gausman.ftl.util.GausmanUtil;

import java.time.Duration;

public class EventListItem {
    private String time;
    private int sectorNumber;
    private int totalBeaconsExplored;
    private int currentBeaconId;
    private int jumpNumber;
    private int eventNumber;
    private SavedGameParser.StoreItemType itemType;
    private Constants.EventType type;
    private int amount;
    private int cost;
    private String id;
    private String text;

    public EventListItem(){
        sectorNumber = 1;
        jumpNumber = 0;
        eventNumber = -1;
    }

    public EventListItem(FTLRun run, FTLSector sector, FTLJump jump, FTLRunEvent event){
        time = GausmanUtil.formatDuration(Duration.between(run.getStartTime(), event.getTs()));
        sectorNumber = sector.getSectorNumber();
        totalBeaconsExplored = jump.getTotalBeaconsExplored();
        currentBeaconId = jump.getCurrentBeaconId();
        jumpNumber = jump.getJumpNumber();
        eventNumber = event.getEventNumber();
        itemType = event.getItemType();
        type = event.getType();
        amount = event.getAmount();
        cost = event.getCost();
        id = event.getId();
        text = GausmanUtil.getTextToId(itemType, id);
    }

    public String getTime() {
        return time;
    }

    public int getSectorNumber() {
        return sectorNumber;
    }

    public int getJumpNumber() {
        return jumpNumber;
    }

    public int getEventNumber() {
        return eventNumber;
    }

    public int getTotalBeaconsExplored() {
        return totalBeaconsExplored;
    }

    public int getCurrentBeaconId() {
        return currentBeaconId;
    }

    public SavedGameParser.StoreItemType getItemType() {
        return itemType;
    }

    public Constants.EventType getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public int getCost() {
        return cost;
    }

    public String getId() {
        return id;
    }

    public String getText(){
        return text;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setSectorNumber(int sectorNumber) {
        this.sectorNumber = sectorNumber;
    }

    public void setTotalBeaconsExplored(int totalBeaconsExplored) {
        this.totalBeaconsExplored = totalBeaconsExplored;
    }

    public void setCurrentBeaconId(int currentBeaconId) {
        this.currentBeaconId = currentBeaconId;
    }

    public void setJumpNumber(int jumpNumber) {
        this.jumpNumber = jumpNumber;
    }

    public void setEventNumber(int eventNumber) {
        this.eventNumber = eventNumber;
    }

    public void setItemType(SavedGameParser.StoreItemType itemType) {
        this.itemType = itemType;
    }

    public void setType(Constants.EventType type) {
        this.type = type;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }
}
