package net.gausman.ftl.model.change;

import net.gausman.ftl.model.Constants;

public class ValueEffect {
    private final Constants.Resource resource;
    private final int value;

    public ValueEffect(Constants.Resource resource, int value) {
        this.resource = resource;
        this.value = value;
    }

    public Constants.Resource getResource() {
        return resource;
    }

    public int getValue() {
        return value;
    }
}
