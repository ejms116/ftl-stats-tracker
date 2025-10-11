package net.gausman.ftl.model.change.effects;

import net.gausman.ftl.model.Constants;

public class StringStatEffect {
    private Constants.General general;
    private String value;

    public StringStatEffect() {}

    public StringStatEffect(Constants.General general, String value) {
        this.general = general;
        this.value = value;
    }

    public Constants.General getGeneral() {
        return general;
    }

    public String getValue() {
        return value;
    }
}
