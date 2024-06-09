package net.gausman.ftl.view;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.run.FTLJump;
import net.gausman.ftl.model.run.FTLRun;
import net.gausman.ftl.model.run.FTLRunEvent;
import net.gausman.ftl.util.GausmanUtil;

import java.time.Duration;

public class EventListItem {
    private String time;
    private int sectorNumber;
    private int totalBeaconsExplored;
    private int currentBeaconId;
    private int jumpNumber;
    private SavedGameParser.StoreItemType itemType;
    private Constants.EventType type;
    private int amount;
    private int cost;
    private String id;
    private String text;

    public EventListItem(FTLRun run, FTLJump jump, FTLRunEvent event){
        time = GausmanUtil.formatDuration(Duration.between(run.getStartTime(), event.getTs()));
        sectorNumber = jump.getSectorNumber();
        totalBeaconsExplored = jump.getTotalBeaconsExplored();
        currentBeaconId = jump.getCurrentBeaconId();
        jumpNumber = jump.getJumpNumber();
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
}
