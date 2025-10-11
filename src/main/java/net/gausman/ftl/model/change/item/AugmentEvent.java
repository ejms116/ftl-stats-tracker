package net.gausman.ftl.model.change.item;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.record.Jump;
import net.gausman.ftl.util.GausmanUtil;

public class AugmentEvent extends ItemEvent {
    public AugmentEvent() {}

    public AugmentEvent(Jump jump, String itemId) {
        super(Constants.EventDetailType.AUGMENT, jump, itemId);
        setItemType(SavedGameParser.StoreItemType.AUGMENT);
    }

    @Override
    public String getDisplayText(){
        return GausmanUtil.getTextToId(SavedGameParser.StoreItemType.AUGMENT, getItemId());
    }
}
