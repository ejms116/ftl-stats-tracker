package net.gausman.ftl.model.change;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.record.Jump;

public class SystemEvent extends Event {
    private SavedGameParser.SystemType type;
    private boolean playerUpgrade;
    private int amount;
    private int newAmount;

    public SystemEvent(){};

    public SystemEvent(String text, Jump jump, SavedGameParser.SystemType type, boolean playerUpgrade, int amount, int newAmount) {
        super(text, jump);
        this.type = type;
        this.playerUpgrade = playerUpgrade;
        this.amount = amount;
        this.newAmount = newAmount;

        String t = "";
        if (getTags().contains(Constants.EventTag.START)) {
//        if (getEventType().equals(Constants.EventType.START)) {
            t = "starting";
        } else if (getTags().contains(Constants.EventTag.BUY)){
//        } else if (getEventType().equals(Constants.EventType.BUY)){
            t = "player bought";
        } else if (playerUpgrade){
            t = "player upgraded";
        } else {
            t = "upgrade event";
        }
        setDisplayText(String.format(
                "%s: %s %d %s",
                type,
                t,
                amount,
                amount == 1 ? "level" : "levels"
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

    public int getAmount() {
        return amount;
    }

    public int getNewAmount() {
        return newAmount;
    }
}
