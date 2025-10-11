package net.gausman.ftl.model.change.effects;

import net.gausman.ftl.model.Constants;

public class IntegerStatEffect {
    private Constants.General general;
    private int value;

    public IntegerStatEffect() {}

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
