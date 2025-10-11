package net.gausman.ftl.model.change.item;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.record.Jump;
import net.gausman.ftl.util.GausmanUtil;

public class WeaponEvent extends ItemEvent {

    public WeaponEvent(){};

    public WeaponEvent(Jump jump, String itemId) {
        super(Constants.EventDetailType.WEAPON, jump, itemId);
        setItemType(SavedGameParser.StoreItemType.WEAPON);
    }

    @Override
    public String getDisplayText(){
        return GausmanUtil.getTextToId(SavedGameParser.StoreItemType.WEAPON, getItemId());
    }
}
