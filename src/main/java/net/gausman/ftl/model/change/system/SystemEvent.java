package net.gausman.ftl.model.change.system;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;
import net.gausman.ftl.util.GausmanUtil;
import org.jfree.data.flow.DefaultFlowDataset;

import java.util.Optional;

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
        String action = String.format("upgraded by %s", amount);
        if (amount == newAmount && !getTags().contains(Constants.EventTag.START)){
            action = "bought";
        }
        setDisplayText(String.format("%s %s, now level: %s", type, action, newAmount));
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

        Constants.ScrapUsedCategory category = Constants.ScrapUsedCategory.SYSTEM_UPGRADE;
        if (amount == newAmount && !getTags().contains(Constants.EventTag.START)){
            category = Constants.ScrapUsedCategory.SYSTEM_BUY;
        }


        model.getSectorMetrics().update(
                getJump().getSector(),
                category,
                mult*-getResourceEffects().getOrDefault(Constants.Resource.SCRAP,0)
        );

        if (getTags().contains(Constants.EventTag.BUY)){
            model.getSectorMetrics().update(
                    getJump().getSector(),
                    apply,
                    getJump().getCurrentBeaconId(),
                    type.getId(),
                    SavedGameParser.StoreItemType.SYSTEM
            );
            model.getSectorMetrics().addFlowMultipleStages(
                    2,
                    3,
                    Constants.Sankey.SCRAP_AVAILABLE.toString(),
                    category.toString(),
                    Constants.Sankey.SHIP_VALUE.toString(),
                    -1*mult*getResourceEffects().getOrDefault(Constants.Resource.SCRAP,0)
            );
        }

        if (getTags().contains(Constants.EventTag.START)){
            int levelBefore = getNewAmount() - getAmount() + 1;
            if (type.equals(SavedGameParser.SystemType.SHIELDS)){
                levelBefore++;
            }
            int buyCostSystem = mult*GausmanUtil.getBuyCostSystem(type.getId());
            int upgradeCostSystem = mult*GausmanUtil.getUpgradeCostSystem(type.getId(), levelBefore , getNewAmount());
            model.getSectorMetrics().addFlowMultipleStages(
                    0,
                    3,
                    Constants.Sankey.SHIP_START.toString(),
                    Constants.ScrapUsedCategory.SYSTEM_BUY.toString(),
                    Constants.Sankey.SHIP_VALUE.toString(),
                    buyCostSystem
            );

            model.getSectorMetrics().addFlowMultipleStages(
                    0,
                    3,
                    Constants.Sankey.SHIP_START.toString(),
                    Constants.ScrapUsedCategory.SYSTEM_UPGRADE.toString(),
                    Constants.Sankey.SHIP_VALUE.toString(),
                    upgradeCostSystem

            );
        }
    }
}
