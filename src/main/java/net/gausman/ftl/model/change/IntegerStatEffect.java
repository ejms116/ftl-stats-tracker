package net.gausman.ftl.model.change;

import net.gausman.ftl.model.Constants;

public class IntegerStatEffect {
    private final Constants.General general;
    private final int value;

    public IntegerStatEffect(Constants.General general, int value) {
        this.general = general;
        this.value = value;
    }

    public Constants.General getGeneral() {
        return general;
    }

    public int getValue() {
        return value;
    }
}
