package net.gausman.ftl.model;

public class ShipSystem {

    private final String id;
    private final String displayText;
    private final String displayType;
    private final boolean subsystem;
    private int level;

    public ShipSystem(String id, String displayText, String displayType, boolean subsystem) {
        this.id = id;
        this.displayText = displayText;
        this.displayType = displayType;
        this.subsystem = subsystem;
    }

    public String getId() {
        return id;
    }

    public String getDisplayText() {
        return displayText;
    }

    public String getDisplayType() {
        return displayType;
    }

    public boolean isSubsystem() {
        return subsystem;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
