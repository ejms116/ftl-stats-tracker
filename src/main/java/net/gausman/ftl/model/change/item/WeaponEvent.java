package net.gausman.ftl.model.change.item;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;
import net.gausman.ftl.util.GausmanUtil;

public class WeaponEvent extends Event {

    public WeaponEvent(){};

    public WeaponEvent(Constants.EventType eventType, int amount, int scrap, String text, Jump jump){
        super(SavedGameParser.StoreItemType.WEAPON, eventType, amount, scrap, text, jump);
    }

    @Override
    public String getDisplayText(){
        return GausmanUtil.getTextToId(getItemType(), getText());
    }
}
