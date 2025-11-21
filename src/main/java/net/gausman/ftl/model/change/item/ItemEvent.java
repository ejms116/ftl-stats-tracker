package net.gausman.ftl.model.change.item;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.Item;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;
import net.gausman.ftl.util.GausmanUtil;

public abstract class ItemEvent extends Event {
    private String itemId;
    private SavedGameParser.StoreItemType itemType;

    public ItemEvent(){}

    public ItemEvent(Constants.EventDetailType eventDetailType, Jump jump, String itemId) {
        super(eventDetailType, jump);
        this.itemId = itemId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public SavedGameParser.StoreItemType getItemType(){
        return itemType;
    }

    public void setItemType(SavedGameParser.StoreItemType itemType) {
        this.itemType = itemType;
    }

    @Override
    public void applyEventToShipStatusModel(ShipStatusModel model, boolean apply) {
        super.applyEventToShipStatusModel(model, apply);
        int mult = apply ? 1 : -1;


        if (this.getTags().contains(Constants.EventTag.BUY) ||
                this.getTags().contains(Constants.EventTag.START) ||
                this.getTags().contains(Constants.EventTag.REWARD)){

            if (apply){
                model.getItemList().add(new Item(getItemId(), getItemType(), convertTagToItemOrigin()));
            } else {
                boolean removed = model.removeMatchingItem(getItemId(), getItemType(), convertTagToItemOrigin(), Constants.ItemState.INVENTORY);
                if (!removed){
                    log.error("Item could not be removed from list.");
                }
            }
            updateSectorMetrics(model, apply, mult);


            if (this.getTags().contains(Constants.EventTag.BUY)){
                int flowValueBuy = -1*mult*getResourceEffects().getOrDefault(Constants.Resource.SCRAP,0);
                model.getSectorMetrics().addFlowMultipleStages(
                        2,
                        3,
                        Constants.Sankey.SCRAP_AVAILABLE.toString(),
                        convertItemTypeToScrapUsedCategory(itemType).toString(),
                        Constants.Sankey.SHIP_VALUE.toString(),
                        flowValueBuy
                );
            }

            if (this.getTags().contains(Constants.EventTag.START)){
                model.getSectorMetrics().addFlowMultipleStages(0, 3,
                        Constants.Sankey.ITEMS_START.toString(),
                        Constants.Sankey.INVENTORY.toString(),
                        Constants.Sankey.SHIP_VALUE.toString(),
                        mult * GausmanUtil.getCostStoreItemId(getItemType(), getItemId())
                );
            }

            if (this.getTags().contains(Constants.EventTag.REWARD)){
                model.getSectorMetrics().addFlowMultipleStages(0, 3,
                        Constants.Sankey.ITEMS_FREE.toString(),
                        Constants.Sankey.INVENTORY.toString(),
                        Constants.Sankey.SHIP_VALUE.toString(),
                        mult * GausmanUtil.getCostStoreItemId(getItemType(), getItemId())
                );
            }

        } else if (this.getTags().contains(Constants.EventTag.SELL) ||
            this.getTags().contains(Constants.EventTag.DISCARD)){
                Item itemChanged;
                if (apply){
                    itemChanged = model.updateItemState(
                            getItemId(),
                            getItemType(),
                            Constants.ItemState.INVENTORY,
                            model.convertEventTypeToItemState(extractTag()),
                            1);
                } else {
                    itemChanged = model.updateItemState(
                            getItemId(),
                            getItemType(),
                            model.convertEventTypeToItemState(extractTag()),
                            Constants.ItemState.INVENTORY,
                            -1);
                }
                if (itemChanged == null){
                    log.error("Item state could not be changed.");
                } else {
                    int fullValue = -1 * mult * GausmanUtil.getCostStoreItemId(getItemType(), getItemId());
                    int flowValueRemoved = mult*getResourceEffects().getOrDefault(Constants.Resource.SCRAP,0);
                    int wastedValue =  - fullValue - flowValueRemoved ;

                    if (this.getTags().contains(Constants.EventTag.SELL)){
                        switch(itemChanged.getOrigin()){
                            case START -> {
                                model.getSectorMetrics().addFlowMultipleStages(0, 3,
                                        Constants.Sankey.ITEMS_START.toString(),
                                        Constants.Sankey.INVENTORY.toString(),
                                        Constants.Sankey.SHIP_VALUE.toString(),
                                        fullValue
                                );
                                model.getSectorMetrics().addFlowMultipleStages(
                                        0,
                                        1,
                                        Constants.Sankey.ITEMS_START.toString(),
                                        Constants.Sankey.ITEMS_SOLD.toString(),
                                        Constants.Sankey.SCRAP_AVAILABLE.toString(),
                                        flowValueRemoved
                                );
                                model.getSectorMetrics().updateFlows(
                                        0,
                                        Constants.Sankey.ITEMS_START.toString(),
                                        Constants.Sankey.WASTED.toString(),
                                        wastedValue
                                );

                            }
                            case BUY, BUY_EVENT -> {
//                                int flowValueBuy = -1*mult*getResourceEffects().getOrDefault(Constants.Resource.SCRAP,0);
                                model.getSectorMetrics().addFlowMultipleStages(
                                        2,
                                        3,
                                        Constants.Sankey.SCRAP_AVAILABLE.toString(),
                                        convertItemTypeToScrapUsedCategory(itemType).toString(),
                                        Constants.Sankey.SHIP_VALUE.toString(),
                                        fullValue
                                );
                                model.getSectorMetrics().updateFlows(
                                        2,
                                        Constants.Sankey.SCRAP_AVAILABLE.toString(),
                                        Constants.Sankey.WASTED.toString(),
                                        wastedValue
                                );

                            }
                            case REWARD -> {
                                model.getSectorMetrics().addFlowMultipleStages(0, 3,
                                        Constants.Sankey.ITEMS_FREE.toString(),
                                        Constants.Sankey.INVENTORY.toString(),
                                        Constants.Sankey.SHIP_VALUE.toString(),
                                        fullValue
                                );
                                model.getSectorMetrics().addFlowMultipleStages(
                                        0,
                                        1,
                                        Constants.Sankey.ITEMS_FREE.toString(),
                                        Constants.Sankey.ITEMS_SOLD.toString(),
                                        Constants.Sankey.SCRAP_AVAILABLE.toString(),
                                        flowValueRemoved
                                );
                                model.getSectorMetrics().updateFlows(
                                        0,
                                        Constants.Sankey.ITEMS_FREE.toString(),
                                        Constants.Sankey.WASTED.toString(),
                                        wastedValue
                                );
                            }
                        }
                    }
                }
        }
    }

    private void updateSectorMetrics(ShipStatusModel model, boolean apply, int mult) {
        model.getSectorMetrics().update(
                getJump().getSector(),
                convertItemTypeToScrapUsedCategory(getItemType()),
                -mult*getResourceEffects().getOrDefault(Constants.Resource.SCRAP, 0)
        );

        if (getTags().contains(Constants.EventTag.REWARD)){
            model.getSectorMetrics().update(
                    getJump().getSector(),
                    Constants.ScrapOrigin.FREE,
                    mult* GausmanUtil.getCostStoreItemId(getItemType(), getItemId())/2
            );
        }

        if (getTags().contains(Constants.EventTag.BUY)){
            model.getSectorMetrics().update(
                    getJump().getSector(),
                    apply,
                    getJump().getCurrentBeaconId(),
                    getItemId(),
                    getItemType()
            );
        }
    }

    private Constants.ScrapUsedCategory convertItemTypeToScrapUsedCategory(SavedGameParser.StoreItemType itemType){
        return switch (itemType){
            case WEAPON -> Constants.ScrapUsedCategory.WEAPONS;
            case DRONE -> Constants.ScrapUsedCategory.DRONES;
            case AUGMENT -> Constants.ScrapUsedCategory.AUGMENTS;
            default -> null;
        };
    }

    private Constants.EventTag extractTag(){
        if (this.getTags().contains(Constants.EventTag.SELL)){
            return Constants.EventTag.SELL;
        }
        if (this.getTags().contains(Constants.EventTag.DISCARD)){
            return Constants.EventTag.DISCARD;
        }
        return Constants.EventTag.BUY;
    }

    private Constants.ItemOrigin convertTagToItemOrigin(){
        if (this.getTags().contains(Constants.EventTag.START)){
            return Constants.ItemOrigin.START;
        }
        if (this.getTags().contains(Constants.EventTag.BUY)){
            return Constants.ItemOrigin.BUY;
        }
        if (this.getTags().contains(Constants.EventTag.REWARD)){
            return Constants.ItemOrigin.REWARD;
        }
        return null;
    }
}
