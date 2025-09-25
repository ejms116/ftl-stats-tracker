package net.gausman.ftl.model.change.item;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.record.Jump;
import net.gausman.ftl.util.GausmanUtil;

public class AugmentEvent extends ItemEvent {
    public AugmentEvent() {}

    public AugmentEvent(String text, Jump jump, String itemId) {
        super(text, jump, itemId);
    }

    @Override
    public String getDisplayText(){
        return GausmanUtil.getTextToId(SavedGameParser.StoreItemType.AUGMENT, getItemId());
    }
}
