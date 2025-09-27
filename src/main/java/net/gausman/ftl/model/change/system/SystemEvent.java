package net.gausman.ftl.model.change.system;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;

public class SystemEvent extends Event {
    private SavedGameParser.SystemType type;
    private boolean playerUpgrade;
    private int amount;
    private int newAmount;

    public SystemEvent(){};

    public SystemEvent(Jump jump, SavedGameParser.SystemType type, boolean playerUpgrade, int amount, int newAmount) {
        super(resolveDetailType(type), jump);
        this.type = type;
        this.playerUpgrade = playerUpgrade;
        this.amount = amount;
        this.newAmount = newAmount;

        setDisplayText(String.format("%s upgraded by %s, now level: %s", type, amount, newAmount));
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

    private static Constants.EventDetailType resolveDetailType(SavedGameParser.SystemType type) {
        if (type.isSubsystem()) {
            return Constants.EventDetailType.SUBSYSTEM;
        } else {
            return Constants.EventDetailType.SYSTEM;
        }
    }

    @Override
    public void applyEventToShipStatusModel(ShipStatusModel model, boolean apply) {
        super.applyEventToShipStatusModel(model, apply);
        int mult = apply ? 1 : -1;

        model.getSystems().compute(type, (k,v) -> v + mult * getAmount());

        model.getSectorMetrics().update(
                getJump().getSector(),
                Constants.ScrapUsedCategory.SYSTEM_BUY, // todo remove system upgrade?
                -getResourceEffects().getOrDefault(Constants.Resource.SCRAP,0)
        );
    }
}
