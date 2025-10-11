package net.gausman.ftl.model.change.item;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.Item;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;
import net.gausman.ftl.util.GausmanUtil;

//@JsonSubTypes({
//        @JsonSubTypes.Type(value = AugmentEvent.class, name = "AugmentEvent"),
//        @JsonSubTypes.Type(value = DroneEvent.class, name = "DroneEvent"),
//        @JsonSubTypes.Type(value = WeaponEvent.class, name = "WeaponEvent"),
//})
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
            updateSectorMetrics(model, mult);

        } else if (this.getTags().contains(Constants.EventTag.SELL) ||
            this.getTags().contains(Constants.EventTag.DISCARD)){

                boolean stateChanged;
                if (apply){
                    stateChanged = model.setStateMatchingItem(
                            getItemId(),
                            getItemType(),
                            Constants.ItemState.INVENTORY,
                            model.convertEventTypeToItemState(extractTag()));

                } else {
                    stateChanged = model.setStateMatchingItem(
                            getItemId(),
                            getItemType(),
                            model.convertEventTypeToItemState(extractTag()),
                            Constants.ItemState.INVENTORY);
                }
                if (!stateChanged){
                    log.error("Item state could not be changed.");
                }
        }

    }

    private void updateSectorMetrics(ShipStatusModel model, int mult) {
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
