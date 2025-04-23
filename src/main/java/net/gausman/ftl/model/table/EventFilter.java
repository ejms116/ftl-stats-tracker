package net.gausman.ftl.model.table;

public enum EventFilter {
    HIDE_FUEL_USED_EVENTS("Hide fuel used events"),
    HIDE_START_EVENTS("Hide run start events"),
    MERGE_EVENTS("Merge similar events"),
    AUTO_SELECT_NEW_EVENT("Auto-select new events")
    ;

    private final String displayName;

    EventFilter(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName(){
        return displayName;
    }
}
