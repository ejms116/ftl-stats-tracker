package net.gausman.ftl.model;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.util.GausmanUtil;

public class Item {
    private final String id;
    private final String text;
    private final SavedGameParser.StoreItemType itemType;
    private final Constants.ItemOrigin origin;
    private Constants.ItemState state;

    public Item(String id, SavedGameParser.StoreItemType itemType, Constants.ItemOrigin origin) {
        this.id = id;
        this.text = GausmanUtil.getTextToId(itemType, id);
        this.itemType = itemType;
        this.origin = origin;
        this.state = Constants.ItemState.INVENTORY;
    }

    // Deep copy
    public Item(Item other) {
        this.id = other.id;
        this.text = GausmanUtil.getTextToId(other.itemType, id);
        this.itemType = other.itemType;
        this.origin = other.origin;
        this.state = other.state;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public SavedGameParser.StoreItemType getItemType() {
        return itemType;
    }

    public Constants.ItemOrigin getOrigin() {
        return origin;
    }

    public Constants.ItemState getState() {
        return state;
    }

    public void setState(Constants.ItemState state) {
        this.state = state;
    }
}
