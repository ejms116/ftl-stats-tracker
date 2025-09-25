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

    public GeneralEvent(String text, Jump jump, Constants.General general) {
        super(text, jump);
        this.general = general;
        this.setDisplayText(this.general.toString());
        if (general.equals(Constants.General.SCRAP_DIFF)){
            this.setDisplayText(text);
        }
    }

    public Constants.General getGeneral() {
        return general;
    }
}
