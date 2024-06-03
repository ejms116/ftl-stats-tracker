package net.gausman.ftl.view;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.run.FTLJump;
import net.gausman.ftl.model.run.FTLRun;
import net.gausman.ftl.model.run.FTLRunEvent;
import net.gausman.ftl.util.DateUtil;

import java.time.Duration;
import java.time.Instant;

public class EventListItem {
    //private Instant ts;
    private String time;
    private int sectorNumber;
    private int totalBeaconsExplored;
    private int currentBeaconId;
    private int jumpNumber;
    private Constants.EventCategory category;
    private Constants.EventType type;
    private int amount;
    private String id;

    public EventListItem(FTLRun run, FTLJump jump, FTLRunEvent event){
        Instant t1 = event.getTs();
        Instant t2 = run.getStartTime();
        Duration dur1 = Duration.between(t1,t2);
        Duration dur2 = Duration.between(t2,t1);
        String res1 = DateUtil.formatDuration(dur1);
        String res2 = DateUtil.formatDuration(dur2);

        time = DateUtil.formatDuration(Duration.between(run.getStartTime(), event.getTs()));
        sectorNumber = jump.getSectorNumber();
        totalBeaconsExplored = jump.getTotalBeaconsExplored();
        currentBeaconId = jump.getCurrentBeaconId();
        jumpNumber = jump.getJumpNumber();
        category = event.getCategory();
        type = event.getType();
        amount = event.getAmount();
        id = event.getId();
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

    public Constants.EventCategory getCategory() {
        return category;
    }

    public Constants.EventType getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public String getId() {
        return id;
    }
}
