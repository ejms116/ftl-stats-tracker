package net.gausman.ftl.model.change;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.record.Jump;

public class SystemEvent extends Event {
    private final SavedGameParser.SystemType type;
    private boolean playerUpgrade;

    public SystemEvent(Constants.EventType eventType, int amount, int scrap, String text, Jump jump, SavedGameParser.SystemType type, boolean playerUpgrade) {
        super(SavedGameParser.StoreItemType.SYSTEM, eventType, amount, scrap, text, jump);
        this.type = type;
        this.playerUpgrade = playerUpgrade;

        String t = "";
        if (getEventType().equals(Constants.EventType.START)){
            t = "starting";
        } else if (playerUpgrade){
            t = "player upgraded";
        } else {
            t = "upgrade event";
        }
        setDisplayText(String.format(
                "%s: %s %d %s",
                type,
                t,
                getAmount(),
                getAmount() == 1 ? "level" : "levels"
        ));
    }

    public SavedGameParser.SystemType getType() {
        return type;
    }

    public boolean isPlayerUpgrade() {
        return playerUpgrade;
    }

    public void setPlayerUpgrade(boolean playerUpgrade) {
        this.playerUpgrade = playerUpgrade;
    }
}
