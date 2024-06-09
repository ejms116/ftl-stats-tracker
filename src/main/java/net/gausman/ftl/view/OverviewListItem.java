package net.gausman.ftl.view;

public class OverviewListItem {
    private String property;
    private String value;

    public OverviewListItem(String property, String value){
        this.property = property;
        this.value = value;
    }

    public String getProperty() {
        return property;
    }

    public String getValue() {
        return value;
    }
}
