package net.gausman.ftl.model.change;

import net.gausman.ftl.model.Constants;

public class StringStatEffect {
    private final Constants.General general;
    private final String value;

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
