package net.gausman.ftl.model.record;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;

public class SystemEvent extends Event {
    private final SavedGameParser.SystemType type;
    private boolean playerUpgrade;

    public SystemEvent(Constants.EventType eventType, int amount, int scrap, String text, Jump jump, SavedGameParser.SystemType type, boolean playerUpgrade) {
        super(SavedGameParser.StoreItemType.SYSTEM, eventType, amount, scrap, text, jump);
        this.type = type;
        this.playerUpgrade = playerUpgrade;
    }

    @Override
    public String getDisplayText(){
        String result = "";
        if (playerUpgrade){
            result = String.format("Player upgraded %s by %s level(s).", type, getAmount());
        } else {
            result = String.format("System upgrade event: %s", type);
        }
        return result;
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
