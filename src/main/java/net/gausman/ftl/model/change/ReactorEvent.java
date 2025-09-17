package net.gausman.ftl.model.change;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.record.Jump;
import net.gausman.ftl.util.GausmanUtil;

public class ReactorEvent extends Event {

    public ReactorEvent(){};

    public ReactorEvent(Constants.EventType eventType, int amount, int scrap, String text, Jump jump){
        super(SavedGameParser.StoreItemType.REACTOR, eventType, amount, scrap, text, jump);
    }

    @Override
    public String getDisplayText(){
        return GausmanUtil.getTextToId(getItemType(), getText());
    }
}
