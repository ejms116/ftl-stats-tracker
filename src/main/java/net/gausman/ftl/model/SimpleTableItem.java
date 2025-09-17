package net.gausman.ftl.model;

public class SimpleTableItem {
    private final String key;
    private final String value;

    public SimpleTableItem(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
