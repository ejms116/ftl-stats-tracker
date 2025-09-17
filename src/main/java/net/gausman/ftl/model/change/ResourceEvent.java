package net.gausman.ftl.model.change;

import com.fasterxml.jackson.annotation.JsonCreator;
import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.record.Jump;
import net.gausman.ftl.util.GausmanUtil;

public class ResourceEvent extends Event {
    @JsonCreator
    public ResourceEvent(){};

    public ResourceEvent(Constants.EventType eventType, int amount, int scrap, String text, Jump jump){
        super(SavedGameParser.StoreItemType.RESOURCE, eventType, amount, scrap, text, jump);
    }

    @Override
    public String getDisplayText(){
        return GausmanUtil.getTextToId(getItemType(), getText());
    }
}
