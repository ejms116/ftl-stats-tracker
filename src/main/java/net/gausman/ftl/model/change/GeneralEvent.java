package net.gausman.ftl.model.change;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.record.Jump;

public class GeneralEvent extends Event {
    private final Constants.General general;

    @JsonCreator
    public GeneralEvent(@JsonProperty("general") Constants.General general){
        super();
        this.general = general;
    }

    public GeneralEvent(Constants.EventType eventType, int amount, int scrap, String text, Jump jump, Constants.General general) {
        super(SavedGameParser.StoreItemType.GENERAL, eventType, amount, scrap, text, jump);
        this.general = general;
        this.setDisplayText(this.general.toString());
        if (general.equals(Constants.General.SCRAP_DIFF)){
            this.setDisplayText(text);
        }
    }

    public GeneralEvent(Constants.EventType eventType, int amount, String text, Jump jump, Constants.General general) {
        super(SavedGameParser.StoreItemType.GENERAL, eventType, amount, 0, text, jump);
        this.general = general;
        this.setDisplayText(this.general.toString());
    }

    public Constants.General getGeneral() {
        return general;
    }
}
