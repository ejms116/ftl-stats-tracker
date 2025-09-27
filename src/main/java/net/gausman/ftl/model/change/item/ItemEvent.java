package net.gausman.ftl.model.change.item;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;

public class ItemEvent extends Event {
    private String itemId;

    public ItemEvent(){}

    public ItemEvent(Constants.EventDetailType eventDetailType, Jump jump, String itemId) {
        super(eventDetailType, jump);
        this.itemId = itemId;
    }

    public String getItemId() {
        return itemId;
    }
}
